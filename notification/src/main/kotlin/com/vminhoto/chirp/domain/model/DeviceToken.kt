package com.vminhoto.chirp.domain.model

import java.time.Instant

/**
 * Data class representing the domain Device Token model.
 * @param id Id of the token
 * @param userId Id of the user this token belongs to
 * @param token The token
 * @param platform the platform for which the token is (ANDROID or IOS)
 * @param createdAt when was the token created.
 */
data class DeviceToken(
    val id: Long,
    val userId: String,
    val token: String,
    val platform: Platform,
    val createdAt: Instant = Instant.now()
) {
    enum class Platform {
        ANDROID,IOS
    }
}
