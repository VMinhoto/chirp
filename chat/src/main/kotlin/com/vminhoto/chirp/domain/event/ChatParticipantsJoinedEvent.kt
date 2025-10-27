package com.vminhoto.chirp.domain.event

import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.UserId

/**
 * Class representing a ChatParticipantsJoinedEvent
 * @param chatId the id of the chat.
 * @param userIds Set of UserIds of the Users that are joining.
 */
data class ChatParticipantsJoinedEvent(
    val chatId: ChatId,
    val userIds: Set<UserId>
)
