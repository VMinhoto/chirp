package com.vminhoto.chirp.domain.event

import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.ChatMessageId

/**
 * Class represented a MessageDeletedEvent
 * @param chatId the id of the chat the message belongs to.
 * @param messageId the id of the message.
 */
data class MessageDeletedEvent(
    val chatId: ChatId,
    val messageId: ChatMessageId
)
