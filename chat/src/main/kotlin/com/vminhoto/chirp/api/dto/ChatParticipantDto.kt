package com.vminhoto.chirp.api.dto

import com.vminhoto.chirp.domain.type.UserId

/**
 * TODO add documentation
 */
data class ChatParticipantDto(
    val userId: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String?
)
