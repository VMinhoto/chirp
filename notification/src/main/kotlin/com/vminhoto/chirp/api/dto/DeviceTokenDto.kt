package com.vminhoto.chirp.api.dto

import com.vminhoto.chirp.domain.type.UserId
import java.time.Instant

data class DeviceTokenDto(
    val userId: UserId,
    val token: String,
    val createdAt: Instant
    )