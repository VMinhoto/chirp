package com.vminhoto.chirp.infra.database.mappers

import com.vminhoto.chirp.domain.model.User
import com.vminhoto.chirp.infra.database.entities.UserEntity

fun UserEntity.toUser(): User {
    return User(
        id = id!!,
        username = username,
        email = email,
        hasEmailVerified = hasVerifiedEmail
    )
}