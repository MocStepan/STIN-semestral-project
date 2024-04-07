package com.tul.backend.auth.base.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoder(
    private val encoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
) {
    fun encode(password: String): String {
        return encoder.encode(password)
    }

    fun matches(
        password: String,
        encodedPassword: String,
    ): Boolean {
        return encoder.matches(password, encodedPassword)
    }
}
