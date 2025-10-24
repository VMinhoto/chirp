package com.vminhoto.chirp.api.mappers

import com.vminhoto.chirp.api.dto.ChatDto
import com.vminhoto.chirp.api.dto.ChatMessageDto
import com.vminhoto.chirp.api.dto.ChatParticipantDto
import com.vminhoto.chirp.domain.models.Chat
import com.vminhoto.chirp.domain.models.ChatMessage
import com.vminhoto.chirp.domain.models.ChatParticipant

/**
 * TODO add documentation
 */
fun Chat.toChatDto(): ChatDto {
    return ChatDto(
        id = id,
        participants = participants.map {
            it.toChatParticipantDto()
        },
        lastActivityAt = lastActivityAt,
        lastMessage = lastMessage?.toChatMessageDto(),
        creator = creator.toChatParticipantDto()

    )
}

fun ChatMessage.toChatMessageDto(): ChatMessageDto {
    return ChatMessageDto(
        id = id,
        chatId = chatId,
        content = content,
        createdAt = createdAt,
        senderId = sender.userId
    )
}

fun ChatParticipant.toChatParticipantDto(): ChatParticipantDto {
    return ChatParticipantDto(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl
    )
}