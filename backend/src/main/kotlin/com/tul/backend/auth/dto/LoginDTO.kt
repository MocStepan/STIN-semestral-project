package com.tul.backend.auth.dto

import com.tul.backend.auth.base.valueobject.EmailAddress

data class LoginDTO(
  val email: EmailAddress,
  val password: String,
) {
  fun isValid(): Boolean {
    return email.isValid() && password.isNotEmpty()
  }
}
