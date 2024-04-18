package com.tul.backend.auth.service

import com.tul.backend.auth.dto.LoginDTO
import com.tul.backend.auth.dto.RegisterDTO
import com.tul.backend.auth.repository.AuthUserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class AuthUserService(
  private val authenticationHandler: AuthenticationHandler,
  private val authUserRepository: AuthUserRepository
) {
  fun login(loginDTO: LoginDTO, request: HttpServletRequest, response: HttpServletResponse): Boolean {
    if (!loginDTO.isValid()) {
      log.warn { "LoginDTO: $loginDTO is invalid" }
      return false
    }

    return authenticationHandler.authenticate(loginDTO, request, response)
  }

  fun register(registerDTO: RegisterDTO): Boolean {
    if (!registerDTO.isValid()) {
      log.warn { "RegisterDTO: $registerDTO is invalid" }
      return false
    }

    val exists = authUserRepository.existsByEmail(registerDTO.email.value)
    if (exists) {
      log.warn { "User with email: ${registerDTO.email} already exists" }
      return false
    }

    authUserRepository.save(
      authenticationHandler.hashRegistrationPassword(registerDTO)
    )
    return true
  }
}
