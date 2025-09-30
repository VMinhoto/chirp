package com.vminhoto.chirp.api.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.vminhoto.chirp.api.util.Password
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

// Corresponds to data coming from a register request.
data class RegisterRequest @JsonCreator constructor(
    @field:Email(message = "Must be a valid email address")
    @JsonProperty("email")
    val email: String,
    @JsonProperty("username")
    @field:Length(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    val username: String,
    @field:Password
    @JsonProperty("password")
    val password: String
)
