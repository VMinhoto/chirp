package com.vminhoto.chirp.infra.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoder {

    private val bCrypt = BCryptPasswordEncoder()

    fun encode(rawPassword: String): String? = bCrypt.encode(rawPassword)

    fun matches(rawPassword: String, hashedPassword: String): Boolean {
        return bCrypt.matches(rawPassword, hashedPassword)
    }
}