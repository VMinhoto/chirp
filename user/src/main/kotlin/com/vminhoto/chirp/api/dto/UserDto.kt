package com.vminhoto.chirp.api.dto

import com.vminhoto.chirp.domain.model.UserId

// This data class corresponds to what the client needs to receive from the server from the values within the User
// database model
data class UserDto(
    val id: UserId,
    val email: String,
    val username: String,
    val hasVerifiedEmail: Boolean,
)
