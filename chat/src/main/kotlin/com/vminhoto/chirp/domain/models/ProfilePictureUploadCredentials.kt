package com.vminhoto.chirp.domain.models

import java.time.Instant

/**
 * Dataclass representing a Profile Picture Upload credentials
 * @param uploadUrl upload link to send the picture.
 * @param publicUrl public URL where the picture will be available.
 * @param headers Headers of the request.
 * @param expiresAt The time when the link expires.
 */
data class ProfilePictureUploadCredentials(
    val uploadUrl: String,
    val publicUrl: String,
    val headers: Map<String, String>,
    val expiresAt: Instant
)
