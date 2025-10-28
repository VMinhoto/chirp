package com.vminhoto.chirp.api.dto.ws

import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.ChatMessageId

/**
 * Data class to hold a Dto for a delete message
 * @param chatId the Id of the chat.
 * @param messageId the Id of the message.
 */
data class DeleteMessageDto(
    val chatId: ChatId,
    val messageId: ChatMessageId
)
