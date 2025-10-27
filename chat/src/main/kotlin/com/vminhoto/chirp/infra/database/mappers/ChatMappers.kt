package com.vminhoto.chirp.infra.database.mappers

import com.vminhoto.chirp.domain.models.Chat
import com.vminhoto.chirp.domain.models.ChatMessage
import com.vminhoto.chirp.domain.models.ChatParticipant
import com.vminhoto.chirp.infra.database.entities.ChatEntity
import com.vminhoto.chirp.infra.database.entities.ChatMessageEntity
import com.vminhoto.chirp.infra.database.entities.ChatParticipantEntity

/**
 * Mapper to transform a [ChatEntity] to a [Chat] domain model.
 * @param lastMessage Latest message in the chat.
 * @return [Chat] Returns the corresponding [Chat] model instance.
 */
fun ChatEntity.toChat(lastMessage: ChatMessage? = null): Chat {
    return Chat(
        id = id!!,
        participants = participants.map {
            it.toChatParticipant()
        }.toSet(),
        creator = creator.toChatParticipant(),
        lastActivityAt = lastMessage?.createdAt ?: createdAt,
        createdAt = createdAt,
        lastMessage = lastMessage
    )
}

/**
 * Mapper to transform a [ChatParticipantEntity] to a [ChatParticipant] domain model.
 * @return [ChatParticipant] Returns the corresponding [ChatParticipant] model instance.
 */
fun ChatParticipantEntity.toChatParticipant(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl
    )
}

/**
 * Mapper to transform a [ChatParticipant] to a [ChatParticipantEntity] entity.
 * @return [ChatParticipantEntity] Returns the corresponding [ChatParticipantEntity] entity instance.
 */
fun ChatParticipant.toChatParticipantEntity(): ChatParticipantEntity {
    return ChatParticipantEntity(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl
    )
}

/**
 * Mapper to transform a [ChatMessageEntity] to a [ChatMessage] domain model.
 * @return [ChatMessage] Returns the corresponding [ChatMessageEntity] entity instance.
 */
fun ChatMessageEntity.toChatMessage(): ChatMessage {
    return ChatMessage(
        id = id!!,
        chatId = chatId,
        sender = sender.toChatParticipant(),
        content = content,
        createdAt = createdAt

    )
}