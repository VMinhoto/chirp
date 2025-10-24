package com.vminhoto.chirp.service

import com.vminhoto.chirp.domain.exception.ChatParticipantNotFoundException
import com.vminhoto.chirp.domain.exception.InvalidChatSizeException
import com.vminhoto.chirp.domain.models.Chat
import com.vminhoto.chirp.domain.type.UserId
import com.vminhoto.chirp.infra.database.entities.ChatEntity
import com.vminhoto.chirp.infra.database.mappers.toChat
import com.vminhoto.chirp.infra.database.repositories.ChatParticipantRepository
import com.vminhoto.chirp.infra.database.repositories.ChatRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service Class to handle chat related operations and transactions with the Chat related repos
 * @property chatRepository Repository for the chat
 * @property chatParticipantRepository Repository for ChatParticipant
 */
@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository
) {

    /**
     * This function creates a chat. It checks if there is a valid chat with more than 1 person.
     * @param creatorId the Id of the creator of the chat
     * @param otherUserIds [Set] of [UserId] corresponding to the Participants to be Added to the chat.
     * @throws InvalidChatSizeException if the total participants are less then 2.
     * @throws ChatParticipantNotFoundException if the a chat participant is not found in the database.
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

}