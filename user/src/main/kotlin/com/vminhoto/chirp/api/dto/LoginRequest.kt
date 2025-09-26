package com.vminhoto.chirp.api.dto

// Corresponds to data coming from a login request.
data class LoginRequest(
    val email: String,
    val password: String
)
