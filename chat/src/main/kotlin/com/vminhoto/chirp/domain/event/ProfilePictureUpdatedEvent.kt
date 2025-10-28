package com.vminhoto.chirp.domain.event

import com.vminhoto.chirp.domain.type.UserId

/**
 * Data class to represent a Profile Picture Updated Event
 * @param userId Id of the user.
 * @param newUrl new URL of the picture.
 */
data class ProfilePictureUpdatedEvent(
    val userId: UserId,
    val newUrl: String?
)