package com.tul.backend.auth.dto

data class LoginDTO(
  val email: String,
  val password: String,
) {
  fun isValid(): Boolean {
    return email.isNotEmpty() && password.isNotEmpty()
  }
}
