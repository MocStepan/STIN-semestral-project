package com.tul.backend.auth.service

import com.tul.backend.auth.base.service.PasswordEncoder
import com.tul.backend.auth.dto.AuthUserDTO
import com.tul.backend.auth.dto.LoginDTO
import com.tul.backend.auth.entity.AuthUser
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class AuthPasswordService(
  private val encoder: PasswordEncoder = PasswordEncoder()
) {

  fun login(loginDTO: LoginDTO, authUser: AuthUser): AuthUserDTO? {
    if(!encoder.matches(loginDTO.password, authUser.password)) {
      log.warn { "Invalid password for user: ${authUser.email.value}" }
      return null
    }

    return null
  }
}