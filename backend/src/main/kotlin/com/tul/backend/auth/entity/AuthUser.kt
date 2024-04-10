package com.tul.backend.auth.entity

import com.tul.backend.auth.base.valueobject.EmailAddress
import com.tul.backend.auth.base.valueobject.UserRole
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
  val email: EmailAddress,
  var password: String,
  @Enumerated(EnumType.STRING)
  val role: UserRole,
) {
  companion object {
    fun from(registerDTO: RegisterDTO): AuthUser {
      return AuthUser(
        username = registerDTO.username,
        email = registerDTO.email,
        password = registerDTO.password,
        role = UserRole.PAID
      )
    }
  }
}
