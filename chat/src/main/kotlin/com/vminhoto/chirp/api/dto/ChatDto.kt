package com.vminhoto.chirp.api.dto

import com.vminhoto.chirp.domain.type.ChatId
import java.time.Instant

/**
 * Dto representing a chat. This class will be used to hold data on API requests and responses.
 * @param id Id of the Chat
 * @param participants List of Chat Participants involved in the Chat.
 * @param lastActivityAt Last activity that this Chat had (creation or message sent)
 * @param lastMessage Last message sent in the chat. Can be null in case there is no message
 * @param creator Chat Participant that created this chat.
 */
data class ChatDto(
    val id: ChatId,
    val participants: List<ChatParticipantDto>,
    val lastActivityAt: Instant,
    val lastMessage: ChatMessageDto?,
    val creator: ChatParticipantDto
)
