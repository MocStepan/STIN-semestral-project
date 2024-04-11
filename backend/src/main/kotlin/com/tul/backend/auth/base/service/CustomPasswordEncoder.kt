package com.tul.backend.auth.base.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class CustomPasswordEncoder(
  private val encoder: BCryptPasswordEncoder = BCryptPasswordEncoder(),
) : PasswordEncoder {

  override fun encode(rawPassword: CharSequence?): String {
    return encoder.encode(rawPassword)
  }

  override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
    return encoder.matches(rawPassword, encodedPassword)
  }
}
