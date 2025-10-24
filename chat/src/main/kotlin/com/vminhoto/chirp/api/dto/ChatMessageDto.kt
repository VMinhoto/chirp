package com.vminhoto.chirp.api.dto

import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.ChatMessageId
import com.vminhoto.chirp.domain.type.UserId
import java.time.Instant

/**
 * TODO add documentation
 */
data class ChatMessageDto(
    val id: ChatMessageId,
    val chatId: ChatId,
    val content: String,
    val createdAt: Instant,
    val senderId: UserId
)
