package com.vminhoto.chirp.domain.models

import com.vminhoto.chirp.domain.type.UserId

/**
 * Data class representing a Chat Participant in the backend system.
 * @property userId The UserId of the Participant
 * @property username The ChatParticipant's username
 * @property email The ChatParticipant's email
 * @property profilePictureUrl The URL for the profile picture of the ChatParticipant if it exists.
 */
data class ChatParticipant(
    val userId: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String?
)