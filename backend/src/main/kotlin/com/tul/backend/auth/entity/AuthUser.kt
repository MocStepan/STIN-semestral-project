package com.tul.backend.auth.entity

import com.tul.backend.auth.base.valueobject.AuthUserRole
import com.tul.backend.auth.base.valueobject.AuthUserRole.USER
import com.tul.backend.auth.base.valueobject.EmailAddress
import com.tul.backend.auth.dto.SignUpDTO
import com.tul.backend.weather.entity.UserWeatherLocation
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class AuthUser(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  val username: String,
  val email: EmailAddress,
  var password: String,
  @Enumerated(EnumType.STRING)
  val role: AuthUserRole = USER,
  @OneToMany(mappedBy = "user")
  val locations: List<UserWeatherLocation> = mutableListOf()
) {
  companion object {
    fun from(signUpDTO: SignUpDTO): AuthUser {
      return AuthUser(
        username = signUpDTO.username,
        email = signUpDTO.email,
        password = signUpDTO.password
      )
    }
  }
}
