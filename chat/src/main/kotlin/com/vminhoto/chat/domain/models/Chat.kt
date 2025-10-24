package com.vminhoto.chat.domain.models

import com.vminhoto.chirp.domain.type.ChatId
import java.time.Instant

/**
 * Data class representing a chat in the backend system.
 * @param id the id of the chat.
 * @param participants A Set of [ChatParticipant] representing the participants that are in the Chat.
 * @param lastMessage The last [ChatMessage] sent int the Chat.
 * @param lastActivityAt The last [Instant] that a message was sent in the Chat.
 * @param createdAt the [Instant] that the Chat was created.
 */
data class Chat(
    val id: ChatId,
    val participants: Set<ChatParticipant>,
    val lastMessage: ChatMessage?,
    val creator: ChatParticipant,
    val lastActivityAt: Instant,
    val createdAt: Instant
)
