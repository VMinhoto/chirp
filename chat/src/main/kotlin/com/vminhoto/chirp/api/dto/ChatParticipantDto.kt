package com.vminhoto.chirp.api.dto

import com.vminhoto.chirp.domain.type.UserId

/**
 * Dto representing a Chat Participant. This class will be used to hold data about a chat participant message on API
 * requests and responses.
 * @param userId Id of the Chat Participant.
 * @param username Username of the Chat Participant.
 * @param email Email of the Chat Participant.
 * @param profilePictureUrl Url where the profile picture of the Chat Participant is. Can be null.
 */
data class ChatParticipantDto(
    val userId: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String?
)
