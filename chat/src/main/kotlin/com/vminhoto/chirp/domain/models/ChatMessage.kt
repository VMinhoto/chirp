package com.vminhoto.chirp.domain.models

import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.ChatMessageId
import java.time.Instant

/**
 * Data class representing a Chat Message in the backend system.
 * @property id Id of the Chat Message.
 * @property chatId Id of the [Chat] this Chat Message belongs to.
 * @property sender The [ChatParticipant] that sent the message.
 * @property content The content of the message.
 * @property createdAt The time the message was created.
 */
data class ChatMessage (
    val id: ChatMessageId,
    val chatId: ChatId,
    val sender: ChatParticipant,
    val content: String,
    val createdAt: Instant
)