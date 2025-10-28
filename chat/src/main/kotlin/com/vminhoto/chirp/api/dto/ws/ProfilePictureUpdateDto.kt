package com.vminhoto.chirp.api.dto.ws

import com.vminhoto.chirp.domain.type.UserId

/**
 * Data class to hold the DTO for the Profile Picture Update DTO
 * @param userId the Id of the user
 * @param newUrl the new public URL.
 */
data class ProfilePictureUpdateDto(
    val userId: UserId,
    val newUrl: String?
)
