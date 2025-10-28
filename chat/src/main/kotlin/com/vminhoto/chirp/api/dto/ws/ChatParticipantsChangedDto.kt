package com.vminhoto.chirp.api.dto.ws

import com.vminhoto.chirp.domain.type.ChatId

/**
 * DTO representing a chat participants changed event.
 * @param chatId the Id of the chat.
 */
data class ChatParticipantsChangedDto(
    val chatId: ChatId
)
