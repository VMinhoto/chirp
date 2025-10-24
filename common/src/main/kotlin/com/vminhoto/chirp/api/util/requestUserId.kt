package com.vminhoto.chirp.api.util

import com.vminhoto.chirp.domain.exceptions.UnauthorizedException
import com.vminhoto.chirp.domain.type.UserId
import org.springframework.security.core.context.SecurityContextHolder

val requestUserId: UserId
    get() = SecurityContextHolder.getContext().authentication?.principal as? UserId
        ?: throw UnauthorizedException()