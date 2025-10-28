package com.vminhoto.chirp.api.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.UserId
import com.vminhoto.chirp.service.ChatMessageService
import com.vminhoto.chirp.service.ChatService
import com.vminhoto.chirp.service.JwtService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
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
     * Data class to hold User Session data.
     * @param userId The id of the user.
     * @param session The webSocket Session.
     */
    private data class UserSession(
        val userId: UserId,
        val session: WebSocketSession
    )
}