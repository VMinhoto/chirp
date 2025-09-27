package com.vminhoto.chirp.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

// Corresponds to data coming from a login request.
data class LoginRequest(
    @JsonProperty("email")
    val email: String,
    @JsonProperty("password")
    val password: String
)
