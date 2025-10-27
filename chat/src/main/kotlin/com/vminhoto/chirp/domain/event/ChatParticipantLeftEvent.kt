package com.vminhoto.chirp.domain.event

import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.UserId

/**
 * Class representing a ChatParticipantLeftEvent
 * @param chatId the id of the chat.
 * @param userId The UserID if the User that is leaving.
 */
data class ChatParticipantLeftEvent(
    val chatId: ChatId,
    val userId: UserId
)
