package com.tul.backend.auth.entity

import com.tul.backend.auth.base.valueobject.AuthUserRole
import com.tul.backend.auth.base.valueobject.EmailAddress
import com.tul.backend.auth.dto.AuthUserBase
import com.tul.backend.auth.dto.RegisterDTO
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class AuthUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val username: String,
    override val email: EmailAddress,
    override var password: String,
    @Enumerated(EnumType.STRING)
    val role: AuthUserRole,
): AuthUserBase {
  companion object {
    fun from(registerDTO: RegisterDTO): AuthUser {
      return AuthUser(
          username = registerDTO.username,
          email = registerDTO.email,
          password = registerDTO.password,
          role = AuthUserRole.USER
      )
    }
  }
}
