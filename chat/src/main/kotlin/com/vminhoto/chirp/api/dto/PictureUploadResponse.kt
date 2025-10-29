package com.vminhoto.chirp.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

/**
 * DTO for the response of Supabase
 * @param uploadUrl
 * @param publicUrl
 * @param headers
 * @param expiresAt
 */
data class PictureUploadResponse(
    @JsonProperty("uploadUrl")
    val uploadUrl: String,
    @JsonProperty("publicUrl")
    val publicUrl: String,
    @JsonProperty("headers")
    val headers: Map<String, String>,
    @JsonProperty("expiresAt")
    val expiresAt: Instant,
)
