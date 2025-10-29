package com.vminhoto.chirp.infra.mappers

import com.vminhoto.chirp.domain.model.DeviceToken
import com.vminhoto.chirp.infra.database.DeviceTokenEntity

fun DeviceTokenEntity.toDeviceToken(): DeviceToken {
    return DeviceToken(
        userId = userId,
        token = token,
        platform = platform.toPlatform(),
        createdAt = createdAt,
        id = id
    )
}