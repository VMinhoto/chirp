package com.vminhoto.chirp.domain.event

import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.UserId

/**
 * Class representing a ChatCreatedEvent
 */
data class ChatCreatedEvent(
    val chatId: ChatId,
    val participantIds: List<UserId>
) {
}