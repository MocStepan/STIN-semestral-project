package com.tul.backend.auth.dto

import com.tul.backend.auth.base.valueobject.EmailAddress

data class SignInDTO(
  val email: EmailAddress,
  val password: String,
) {
  fun isValid(): Boolean {
    return email.isValid() && password.isNotEmpty()
  }
}
