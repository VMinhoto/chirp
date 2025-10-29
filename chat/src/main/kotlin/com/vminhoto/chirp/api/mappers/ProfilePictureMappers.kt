package com.vminhoto.chirp.api.mappers

import com.vminhoto.chirp.api.dto.PictureUploadResponse
import com.vminhoto.chirp.domain.models.ProfilePictureUploadCredentials

fun ProfilePictureUploadCredentials.toResponse(): PictureUploadResponse {
    return PictureUploadResponse(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers,
        expiresAt = expiresAt
    )
}