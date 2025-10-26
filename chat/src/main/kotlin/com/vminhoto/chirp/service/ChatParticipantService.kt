package com.vminhoto.chirp.service

import com.vminhoto.chirp.domain.exception.ChatParticipantNotFoundException
import com.vminhoto.chirp.domain.exception.InvalidChatSizeException
import com.vminhoto.chirp.domain.models.ChatParticipant
import com.vminhoto.chirp.domain.type.UserId
import com.vminhoto.chirp.infra.database.mappers.toChatParticipant
import com.vminhoto.chirp.infra.database.mappers.toChatParticipantEntity
import com.vminhoto.chirp.infra.database.repositories.ChatParticipantRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

/**
 *
 * Service Class to handle chat participant related operations and transactions with the Chat related repos
 * @property chatParticipantRepository Repository for ChatParticipant
 *
 */
@Service
class ChatParticipantService(
    private val chatParticipantRepository: ChatParticipantRepository
) {
    /**
     * This function creates a Chat Participant.
     * @param chatParticipant the chat participant.
     */
    fun createChatParticipant(
        chatParticipant: ChatParticipant
    ){
        chatParticipantRepository.save(
            chatParticipant.toChatParticipantEntity()
        )
    }

    /**
     * This function finds a [ChatParticipant] by [UserId].
     * @param userId the [userId] of the Chat Participant.
     * @return [ChatParticipant] Returns the chat participant with that UserId or Null.
     */
    fun findChatParticipantById(userId: UserId): ChatParticipant? {
        return chatParticipantRepository.findByIdOrNull(userId)?.toChatParticipant()
    }

    /**
     * This function finds a [ChatParticipant] by a query containing the username or email of the user.
     * @param query containing the username or email.
     * @return [ChatParticipant] Returns the chat participant with that username or email.
     */
    fun findChatParticipantByUsernameOrEmail(
        query: String
    ): ChatParticipant? {
        val normalizedQuery = query.lowercase().trim()
        return chatParticipantRepository.findByEmailOrUsername(
            query = normalizedQuery
        )?.toChatParticipant()

    }
}