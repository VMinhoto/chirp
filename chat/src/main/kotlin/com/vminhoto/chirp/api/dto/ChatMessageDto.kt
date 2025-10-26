package com.vminhoto.chirp.api.dto

import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.ChatMessageId
import com.vminhoto.chirp.domain.type.UserId
import java.time.Instant

/**
 * Dto representing a Chat Message. This class will be used to hold data about a chat message on API requests and
 * responses.
 * @param id Id of the Chat message.
 * @param chatId Id of the Chat this message belongs to.
 * @param content The content of the message represented by a String.
 * @param createdAt The Instant this message was created.
 * @param senderId The UserId of the sender of the message.
 *
 */
data class ChatMessageDto(
    val id: ChatMessageId,
    val chatId: ChatId,
    val content: String,
    val createdAt: Instant,
    val senderId: UserId
)
