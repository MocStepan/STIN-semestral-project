package com.tul.backend.auth.dto

import com.tul.backend.auth.base.valueobject.EmailAddress

data class LoginDTO(
    override val email: EmailAddress,
    override val password: String,
): AuthUserBase {
  fun isValid(): Boolean {
    return email.isValid() && password.isNotEmpty()
  }
}
