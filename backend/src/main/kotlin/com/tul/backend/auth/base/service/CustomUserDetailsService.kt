package com.tul.backend.auth.base.service

import com.tul.backend.auth.entity.AuthUser
import com.tul.backend.auth.repository.AuthUserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomUserDetailsService(
  private val authUserRepository: AuthUserRepository
) : UserDetailsService {
  override fun loadUserByUsername(email: String?): UserDetails {
    return authUserRepository.findByEmail(email!!)
      ?.mapToUserDetails()
      ?: throw Exception("User not found")
  }

  private fun AuthUser.mapToUserDetails(): UserDetails {
    return User.builder()
      .username(email.value)
      .password(password)
      .roles(this.role.name)
      .build()
  }
}
