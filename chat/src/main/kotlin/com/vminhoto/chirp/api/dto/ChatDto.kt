package com.vminhoto.chirp.api.dto

import com.vminhoto.chirp.domain.type.ChatId
import java.time.Instant

/**
 * TODO add documentation
 */
data class ChatDto(
    val id: ChatId,
    val participants: List<ChatParticipantDto>,
    val lastActivityAt: Instant,
    val lastMessage: ChatMessageDto?,
    val creator: ChatParticipantDto
)
