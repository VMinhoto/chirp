package com.vminhoto.chirp.api.dto.ws

import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.ChatMessageId

/**
 * What the client will send to the server when the user sends a message.
 * @param chatId
 * @param content
 * @param messageId
 */
class SendMessageDto(
    val chatId: ChatId,
    val content: String,
    val messageId: ChatMessageId? = null,
) {
}