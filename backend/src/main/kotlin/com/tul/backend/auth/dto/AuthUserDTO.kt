package com.tul.backend.auth.dto

import com.tul.backend.auth.base.valueobject.EmailAddress
import com.tul.backend.auth.entity.AuthUser

data class AuthUserDTO(
    val id: Long,
    val username: String,
    val email: EmailAddress
) {
  companion object {
    fun from(authUser: AuthUser): AuthUserDTO {
      return AuthUserDTO(
          id = authUser.id,
          username = authUser.username,
          email = authUser.email,
      )
    }
  }
}
