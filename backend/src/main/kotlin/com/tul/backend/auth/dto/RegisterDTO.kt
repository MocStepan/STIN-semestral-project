package com.tul.backend.auth.dto

import com.tul.backend.auth.base.valueobject.EmailAddress

data class RegisterDTO(
    val username: String,
    val email: EmailAddress,
    val password: String,
) {
  fun isValid(): Boolean =
      username.isNotBlank() && password.isNotBlank() && email.isValid()
}
