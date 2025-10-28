package com.vminhoto.chirp.api.dto.ws

/**
 * Data class to represent a Error and send it on Broadcast Error.
 * @param code
 * @param message
 */
data class ErrorDto(
    val code: String,
    val message: String
)
