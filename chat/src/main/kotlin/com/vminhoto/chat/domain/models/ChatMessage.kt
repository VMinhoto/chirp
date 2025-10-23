package com.vminhoto.chat.domain.models

import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.ChatMessageId
import java.time.Instant

data class ChatMessage (
    val id: ChatMessageId,
    val charId: ChatId,
    val sender: ChatParticipant,
    val content: String,
    val createdAt: Instant
)