package com.vminhoto.chirp.domain.model

import com.vminhoto.chirp.domain.type.UserId

data class User(
    val id: UserId,
    val username: String,
    val email: String,
    val hasEmailVerified: Boolean
)
