package com.vminhoto.chirp.api.websocket

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.vminhoto.chirp.api.dto.ws.ChatParticipantsChangedDto
import com.vminhoto.chirp.api.dto.ws.DeleteMessageDto
import com.vminhoto.chirp.api.dto.ws.ErrorDto
import com.vminhoto.chirp.api.dto.ws.IncomingWebSocketMessage
import com.vminhoto.chirp.api.dto.ws.IncomingWebSocketMessageType
import com.vminhoto.chirp.api.dto.ws.OutgoingWebSocketMessage
import com.vminhoto.chirp.api.dto.ws.OutgoingWebSocketMessageType
import com.vminhoto.chirp.api.dto.ws.ProfilePictureUpdateDto
import com.vminhoto.chirp.api.dto.ws.SendMessageDto
import com.vminhoto.chirp.api.mappers.toChatMessageDto
import com.vminhoto.chirp.domain.event.ChatCreatedEvent
import com.vminhoto.chirp.domain.event.ChatParticipantLeftEvent
import com.vminhoto.chirp.domain.event.ChatParticipantsJoinedEvent
import com.vminhoto.chirp.domain.event.MessageDeletedEvent
import com.vminhoto.chirp.domain.event.ProfilePictureUpdatedEvent
import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.UserId
import com.vminhoto.chirp.service.ChatMessageService
import com.vminhoto.chirp.service.ChatService
import com.vminhoto.chirp.service.JwtService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.PingMessage
import org.springframework.web.socket.PongMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * Handler for Chat Web socket.
 * @param chatMessageService
 * @param chatService
 * @param jwtService
 * @param objectMapper
 */
@Component
class ChatWebSocketHandler(
    private val chatMessageService: ChatMessageService,
    private val objectMapper: ObjectMapper,
    private val chatService: ChatService,
    private val jwtService: JwtService
    ): TextWebSocketHandler() {

    companion object {
        private const val PING_INTERVAL_MS = 30_000L
        private const val PONG_TIMEOUT_MS = 60_000L
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    private val connectionLock = ReentrantReadWriteLock()

    // Hash map to store all active sessions.
    private val sessions = ConcurrentHashMap<String, UserSession>()
    // Hash map to that stores a map of what sessions belong to a certain user.
    private val userToSessions = ConcurrentHashMap<UserId, MutableSet<String>>()
    // Hash map to map a User to all the chats he is in.
    private val userChatIds = ConcurrentHashMap<UserId, MutableSet<ChatId>>()
    // Hash map relating a ChatId with all active sockets connected to that Chat.
    private val chatToSessions = ConcurrentHashMap<ChatId, MutableSet<String>>()


    /**
     * Function to perform actions after a connection is Established. It fills the HashMaps on new sockets created.
     * @param session
     */
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val authHeader = session
            .handshakeHeaders
            .getFirst(HttpHeaders.AUTHORIZATION)
            ?: run {
                logger.warn("Session ${session.id} was closed due to missing Authorization header")
                session.close(CloseStatus.SERVER_ERROR.withReason("Authentication failed"))
                return
            }

        val userId = jwtService.getUserIdFromToken(authHeader)

        val userSession = UserSession(
            userId = userId,
            session = session
        )

        connectionLock.write {
            sessions[session.id] = userSession

            // Perform a read and a write on a concurrent hash map.
            userToSessions.compute(userId) {_, existing_Sessions ->
                (existing_Sessions ?: mutableSetOf()).apply {
                    add(session.id)
                }
            }

            val chatIds = userChatIds.computeIfAbsent(userId) {
                val chatIds = chatService.findChatsByUser(userId).map {it.id}
                ConcurrentHashMap.newKeySet<ChatId>().apply {
                    addAll(chatIds)
                }
            }

            chatIds.forEach { chatId ->
                chatToSessions.compute(chatId) { _, sessions ->
                    (sessions ?: mutableSetOf()).apply {
                        add(session.id)
                    }
                }
            }
        }

        logger.info("Websocket connection established for user $userId")
    }

    /**
     * Function to handle connection closed events. Updates the hashmaps accordingly.
     * @param session
     * @param status
     */
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        connectionLock.write {
            sessions.remove(session.id)?.let { userSession ->
                val userId = userSession.userId

                userToSessions.compute(userId) {_, sessions ->
                    sessions
                        ?.apply { remove(session.id) }
                        ?.takeIf { it.isNotEmpty() }
                }

                userChatIds[userId]?.forEach { chatId ->
                    chatToSessions.compute(chatId) {_, sessions ->
                        sessions
                            ?.apply { remove(session.id) }
                            ?.takeIf { it.isNotEmpty() }
                    }
                }

                logger.info("Websocket session close for user $userId")
            }
        }
    }

    /**
     * Function to handle transport errors. Closes the session if an error occurs.
     * @param session
     * @param exception
     */
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.error("Transport error for session ${session.id}", exception)
        session.close(CloseStatus.SERVER_ERROR.withReason("Transport error"))
    }

    /**
     * Function to handle when a message is received from a client. It tries to read the payload and then gets the
     * payload and broadcasts it to all subscribed users.
     * @param session
     * @param message the received message.
     */
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        logger.debug("Received message ${message.payload}")

        val userSession = connectionLock.read {
            sessions[session.id] ?: return
        }

        try {
            val webSocketMessage = objectMapper.readValue(
                message.payload,
                IncomingWebSocketMessage::class.java
            )
            when (webSocketMessage.type) {
                IncomingWebSocketMessageType.NEW_MESSAGE -> {
                    val dto = objectMapper.readValue(
                        webSocketMessage.payload,
                        SendMessageDto::class.java
                    )
                    handleSendMessage(
                        dto = dto,
                        senderId = userSession.userId
                    )
                }
            }
        } catch (e: JsonMappingException) {
            logger.warn("Could not parse message ${message.payload}", e)
            sendError(
                session = userSession.session,
                error = ErrorDto(
                    code = "INVALID_JSON",
                    message = "Incoming JSON or UUID is invalid"
                )
            )
        }
    }

    /**
     * Function to broadcast to all listening members of the chat that a message was deleted.
     * @param event representing a message deleted event.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onDeleteMessage(event: MessageDeletedEvent){
        broadcastToChat(
            chatId = event.chatId,
            message = OutgoingWebSocketMessage(
                type = OutgoingWebSocketMessageType.MESSAGE_DELETED,
                payload = objectMapper.writeValueAsString(
                    DeleteMessageDto(
                        chatId = event.chatId,
                        messageId = event.messageId
                    )
                )
            )
        )
    }

    /**
     * Function to broadcast to all listening members that a participant joined chat. Updates the hashmaps.
     * @param event representing a mparticipant joined event.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onJoinChat(event: ChatParticipantsJoinedEvent) {
        connectionLock.write {
            connectionLock.write {
                updateChatForUsers(
                    chatId = event.chatId,
                    userIds = event.userIds.toList()
                )
            }
        }

        broadcastToChat(
            chatId = event.chatId,
            message = OutgoingWebSocketMessage(
                type = OutgoingWebSocketMessageType.CHAT_PARTICIPANTS_CHANGED,
                payload = objectMapper.writeValueAsString(
                    ChatParticipantsChangedDto(
                        chatId = event.chatId
                    )
                )
            )
        )
    }

    /**
     * Function to update the Hash maps when a chat is created.
     * @param event representing a chat created event.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onChatCreated(event: ChatCreatedEvent) {
        connectionLock.write {
            updateChatForUsers(
                chatId = event.chatId,
                userIds = event.participantIds
            )
        }
    }

    private fun updateChatForUsers(
        chatId: ChatId,
        userIds: List<UserId>
    ) {
        userIds.forEach { userId ->
            userChatIds.compute(userId) {_, chatIds ->
                (chatIds ?: mutableSetOf()).apply {
                    add(chatId)
                }
            }

            userToSessions[userId]?.forEach { sessionId ->
                chatToSessions.compute(chatId) {_, sessions ->
                    (sessions ?: mutableSetOf()).apply {add(sessionId)}
                }
            }
        }
    }

    /**
     * Function to handle pong message and update the last pong timestamp on the sessions.
     * @param session
     * @param message
     */
    override fun handlePongMessage(session: WebSocketSession, message: PongMessage) {
        connectionLock.write {
            sessions.compute(session.id) {_, userSession ->
                userSession?.copy(
                    lastPongTimestamp = System.currentTimeMillis()
                )
            }
        }
        logger.debug("Received pong from ${session.id}")
    }

    /**
     * Function to periodically ping all clients with open connections.
     */
    @Scheduled(fixedDelay = PING_INTERVAL_MS)
    fun pingClients() {
        val currentTime = System.currentTimeMillis()
        val sessionsToClose = mutableListOf<String>()

        val sessionsSnapshot = connectionLock.read { sessions.toMap() }

        sessionsSnapshot.forEach { (sessionId, userSession) ->
            try {
                if(userSession.session.isOpen) {
                    val lastPong = userSession.lastPongTimestamp
                    if (currentTime - lastPong > PONG_TIMEOUT_MS) {
                        logger.warn("Session $sessionId has timed out, closing connection.")
                        sessionsToClose.add(sessionId)
                        return@forEach
                    }

                    userSession.session.sendMessage(PingMessage())
                    logger.debug("Send ping to {}", userSession.userId)
                }
            } catch (e: Exception){
                logger.error("Could not ping session $sessionId", e)
                sessionsToClose.add(sessionId)
            }
        }

        sessionsToClose.forEach { sessionId ->
            connectionLock.read {
                sessions[sessionId]?.session?.let { session ->
                    try {
                        session.close(CloseStatus.GOING_AWAY.withReason("Ping timeout."))
                    } catch(e: Exception) {
                        logger.error("Couldn't close session for session $sessionId", e)
                    }
                }
            }
        }
    }

    /**
     * Function to broadcast to all listening members of the chat that a participant left chat.
     * @param event representing a user left chat event.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onLeftChat(event: ChatParticipantLeftEvent) {
        connectionLock.write {
            userChatIds.compute(event.userId) { _, chatIds ->
                chatIds
                    ?.apply { remove(event.chatId) }
                    ?.takeIf { it.isNotEmpty() }
            }
        }

        userToSessions[event.userId]?.forEach { sessionId ->
            chatToSessions.compute(event.chatId) {_, sessions ->
                sessions
                    ?.apply { remove(sessionId) }
                    ?.takeIf { it.isNotEmpty() }
            }
        }

        broadcastToChat(
            chatId = event.chatId,
            message = OutgoingWebSocketMessage(
                type = OutgoingWebSocketMessageType.CHAT_PARTICIPANTS_CHANGED,
                payload = objectMapper.writeValueAsString(
                    ChatParticipantsChangedDto(
                        chatId = event.chatId
                    )
                )
            )
        )
    }

    /**
     * Function to broadcast to all listening members of the chat that a participant updates its profile picture.
     * @param event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onProfilePictureUpdated(event: ProfilePictureUpdatedEvent) {
        val useChats = connectionLock.read {
            userChatIds[event.userId]?.toList() ?: emptyList()
        }

        val dto = ProfilePictureUpdateDto(
            userId = event.userId,
            newUrl = event.newUrl
        )

        val sessionIds = mutableSetOf<String>()
        useChats.forEach { chatId ->
            connectionLock.read {
                chatToSessions[chatId]?.let { sessions ->
                    sessionIds.addAll(sessions)
                }
            }
        }

        val webSocketMessage = OutgoingWebSocketMessage(
            type = OutgoingWebSocketMessageType.PROFILE_PICTURE_UPDATED,
            payload = objectMapper.writeValueAsString(dto)
        )
        val messageJson = objectMapper.writeValueAsString(webSocketMessage)

        sessionIds.forEach { sessionId ->
            val userSession = connectionLock.read {
                sessions[sessionId]
            } ?: return@forEach
            try {
                if(userSession.session.isOpen) {
                    userSession.session.sendMessage(TextMessage(messageJson))
                }
            } catch (e: Exception) {
                logger.error("Could not send profile picture update to session $sessionId", e)
            }
        }
    }

    private fun sendError(
        session: WebSocketSession,
        error: ErrorDto
    ) {
        val webSocketMessage = objectMapper.writeValueAsString(
            OutgoingWebSocketMessage(
                type = OutgoingWebSocketMessageType.ERROR,
                payload = objectMapper.writeValueAsString(error)
            )
        )

        try {
            session.sendMessage(TextMessage(webSocketMessage))
        } catch (e: Exception) {
            logger.warn("Couldn't send error message", e)
        }
    }

    private fun broadcastToChat(
        chatId: ChatId,
        message: OutgoingWebSocketMessage
    ) {
        val chatSessions = connectionLock.read {
            chatToSessions[chatId]?.toList() ?: emptyList()
        }

        chatSessions.forEach {sessionId ->
            val userSession = connectionLock.read {
                sessions[sessionId]
            } ?: return@forEach

            sendToUser(
                userId = userSession.userId,
                message = message
            )
        }
    }

    private fun handleSendMessage(
        dto: SendMessageDto,
        senderId: UserId
    ) {
        val userChatsIds = connectionLock.read { this@ChatWebSocketHandler.userChatIds[senderId] } ?: return

        if(dto.chatId !in userChatsIds){
            return
        }

        val savedMessage = chatMessageService.sendMessage(
            chatId = dto.chatId,
            senderId = senderId,
            content = dto.content,
            messageId = dto.messageId
        )

        broadcastToChat(
            chatId = dto.chatId,
            message = OutgoingWebSocketMessage(
                type = OutgoingWebSocketMessageType.NEW_MESSAGE,
                payload = objectMapper.writeValueAsString(
                    savedMessage.toChatMessageDto()
                )
            )
        )
    }

    private fun sendToUser(userId: UserId, message: OutgoingWebSocketMessage) {
        val userSessions = connectionLock.read {
            userToSessions[userId] ?: emptySet()
        }
        userSessions.forEach { sessionId ->
            val userSession = connectionLock.read {
                sessions[sessionId] ?: return@forEach
            }
            if(userSession.session.isOpen) {
                try {
                    val messageJson = objectMapper.writeValueAsString(message)
                    userSession.session.sendMessage(TextMessage(messageJson))
                    logger.info("Sent message")
                } catch (e: Exception) {
                    logger.error("Error while sending message to user $userId", e)
                }
            }
        }
    }

    /**
     * Data class to hold User Session data.
     * @param userId The id of the user.
     * @param session The webSocket Session.
     */
    private data class UserSession(
        val userId: UserId,
        val session: WebSocketSession,
        val lastPongTimestamp: Long = System.currentTimeMillis()
    )
}