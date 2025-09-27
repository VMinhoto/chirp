package com.vminhoto.chirp.infra.database.mappers

import com.vminhoto.chirp.domain.model.EmailVerificationToken
import com.vminhoto.chirp.infra.database.entities.EmailVerificationTokenEntity

fun EmailVerificationTokenEntity.toEmailVerificationToken(): EmailVerificationToken{
    return EmailVerificationToken(
        id = id,
        token = token,
        user = user.toUser(),
    )
}