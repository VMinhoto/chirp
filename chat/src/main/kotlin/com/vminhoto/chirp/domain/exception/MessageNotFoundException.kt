package com.vminhoto.chirp.domain.exception

import com.vminhoto.chirp.domain.type.ChatMessageId

/**
 * Exception to be thrown when a chat is not found.
 */
class MessageNotFoundException(id: ChatMessageId): RuntimeException(
    "Message with the ID $id not found"
) {
}