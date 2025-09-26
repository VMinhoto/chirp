package com.vminhoto.chirp.api.mappers

import com.vminhoto.chirp.api.dto.AuthenticatedUserDto
import com.vminhoto.chirp.api.dto.UserDto
import com.vminhoto.chirp.domain.model.AuthenticatedUser
import com.vminhoto.chirp.domain.model.User

fun AuthenticatedUser.toAuthenticatedUserDto(): AuthenticatedUserDto {
    return AuthenticatedUserDto(
        user = user.toUserDto(),
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}

fun User.toUserDto(): UserDto {
    return UserDto(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasEmailVerified,
    )
}