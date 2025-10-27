package com.vminhoto.chirp.service

import com.vminhoto.chirp.api.dto.ChatMessageDto
import com.vminhoto.chirp.api.mappers.toChatMessageDto
import com.vminhoto.chirp.domain.event.ChatParticipantLeftEvent
import com.vminhoto.chirp.domain.event.ChatParticipantsJoinedEvent
import com.vminhoto.chirp.domain.exception.ChatNotFoundException
import com.vminhoto.chirp.domain.exception.ChatParticipantNotFoundException
import com.vminhoto.chirp.domain.exception.InvalidChatSizeException
import com.vminhoto.chirp.domain.exceptions.ForbiddenException
import com.vminhoto.chirp.domain.models.Chat
import com.vminhoto.chirp.domain.models.ChatMessage
import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.UserId
import com.vminhoto.chirp.infra.database.entities.ChatEntity
import com.vminhoto.chirp.infra.database.mappers.toChat
import com.vminhoto.chirp.infra.database.mappers.toChatMessage
import com.vminhoto.chirp.infra.database.repositories.ChatMessageRepository
import com.vminhoto.chirp.infra.database.repositories.ChatParticipantRepository
import com.vminhoto.chirp.infra.database.repositories.ChatRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * Service Class to handle chat related operations and transactions with the Chat related repos
 * @property chatRepository Repository for the chat
 * @property chatParticipantRepository Repository for ChatParticipant
 * @property chatMessageRepository Repository of the ChatMessage
 * @property applicationEventPublisher
 */
@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    /**
     * Function to get messages (paginated) before a certain Insatant.
     * @param chatId Id of the chat
     * @param before the instant before which messages are loaded
     * @param pageSize size of the page.
     * @return Chat Message Dto do be sent to the client
     */
    fun getChatMessages(
        chatId: ChatId,
        before: Instant?,
        pageSize: Int
    ): List<ChatMessageDto> {
        return chatMessageRepository
            .findByChatIdBefore(
                chatId = chatId,
                before = before ?: Instant.now(),
                pageable = PageRequest.of(0, pageSize)
            )
            .content
            .asReversed()
            .map { it.toChatMessage().toChatMessageDto() }
    }

    /**
     * This function creates a chat. It checks if there is a valid chat with more than 1 person.
     * @param creatorId the Id of the creator of the chat
     * @param otherUserIds [Set] of [UserId] corresponding to the Participants to be Added to the chat.
     * @throws InvalidChatSizeException if the total participants are less than 2.
     * @throws ChatParticipantNotFoundException if the chat participant is not found in the database.
     */
    @Transactional
    fun createChat(
        creatorId: UserId,
        otherUserIds: Set<UserId>
    ): Chat {
        val otherParticipants = chatParticipantRepository.findByUserIdIn(
            userIds = otherUserIds
        )

        val allParticipants = (otherParticipants + creatorId)

        if(allParticipants.size <2) {
            throw InvalidChatSizeException()
        }

        val creator = chatParticipantRepository.findByIdOrNull(creatorId)
            ?: throw ChatParticipantNotFoundException(creatorId)

        return chatRepository.save(
            ChatEntity(
                creator = creator,
                participants = setOf(creator) + otherParticipants
            )
        ).toChat(lastMessage = null)
    }

    /**
     * Function that adds Participants to a chat. Also fires an event through the application event mechanism.
     * @param requestUserId user that is performing the request
     * @param chatId Id of the chat the message is to be added.
     * @param userIds a set of UserIds corresponding to the users to be added.
     * @return a Chat domain model instance of the updated chat.
     * @throws ForbiddenException if the user requesting the operation is not in the Chat.
     * @throws ChatParticipantNotFoundException if any of the users to be added doen't exist.
     */
    @Transactional
    fun addParticipantsToChat(
        requestUserId: UserId,
        chatId: ChatId,
        userIds: Set<UserId>
    ): Chat {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundException()

        // Check if the user requesting the change is in the chat
        val isRequestingUserInChat = chat.participants.any {
            it.userId == requestUserId
        }

        if (!isRequestingUserInChat) {
            throw ForbiddenException()
        }

        // Finds if new users to be added exist.
        val users = userIds.map { userId ->
            chatParticipantRepository.findByIdOrNull(userId)
                ?: throw ChatParticipantNotFoundException(userId)
        }

        // Update Chat
        val lastMessage = lastMessageForChat(chatId)
        val updatedChat = chatRepository.save(
            chat.apply {
                this.participants = chat.participants + users
            }
        ).toChat(lastMessage = lastMessage)

        applicationEventPublisher.publishEvent(
            ChatParticipantsJoinedEvent(
                chatId = chatId,
                userIds = userIds
            )
        )

        return updatedChat
    }

    /**
     * Function to remove a participant from the chat. If the remaining Chat participants in the chat is equal to 0,
     * remove the chat instead. Cascades deletes across schema (on chat messages and cross table). Also fires an event
     * through the application event mechanism.
     * @param chatId Id of the chat where the user is to be removed
     * @param userId Id of the user to be removed
     * @throws ChatParticipantNotFoundException if the user to be removed does not exist in the chat.
     * @throws ChatNotFoundException if the chat does not exist.
     */
    @Transactional
    fun removeParticipantFromChat(
        chatId: ChatId,
        userId: UserId
    ) {
        // Checks if the chat exists
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundException()

        // Checks if the Chat Participant exists in the chat.
        val participant = chat.participants.find { it.userId == userId }
            ?: throw ChatParticipantNotFoundException(userId)

        // Checks if the Chat will be empty
        val newParticipantsSize = chat.participants.size - 1
        if(newParticipantsSize == 0) {
            chatRepository.deleteById(chatId)
            return
        }

        chatRepository.save(
            chat.apply {
                this.participants = chat.participants - participant
            }
        )

        applicationEventPublisher.publishEvent(
            ChatParticipantLeftEvent(
                chatId = chatId,
                userId = userId
            )
        )
    }

    /**
     * Helper function to retrieve the last Message for a particular Chat.
     * @param chatId the Id of the chat
     * @return a chat message if there is one, or null if there isn't any.
     */
    private fun lastMessageForChat(chatId: ChatId): ChatMessage? {
        return chatMessageRepository
            .findLatestMessagesByChatIds(setOf(chatId))
            .firstOrNull()
            ?.toChatMessage()
    }

}