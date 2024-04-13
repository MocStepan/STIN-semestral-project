package com.tul.backend.auth.service

import com.tul.backend.auth.dto.AuthUserDTO
import com.tul.backend.auth.dto.LoginDTO
import com.tul.backend.auth.dto.RegisterDTO
import com.tul.backend.auth.repository.AuthUserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
@Transactional
class AuthUserService(
  private val authenticationHandler: AuthenticationHandler,
  private val authUserRepository: AuthUserRepository
) {
  fun login(loginDTO: LoginDTO, request: HttpServletRequest, response: HttpServletResponse): String? {
    if (!loginDTO.isValid()) {
      log.warn { "LoginDTO: $loginDTO is invalid" }
      return null
    }

    return authenticationHandler.authenticate(loginDTO, request, response)
  }

  fun register(registerDTO: RegisterDTO): AuthUserDTO? {
    if (!registerDTO.isValid()) {
      log.warn { "RegisterDTO: $registerDTO is invalid" }
      return null
    }

    val exists = authUserRepository.existsByEmail(registerDTO.email.value)
    if (exists) {
      log.warn { "User with email: ${registerDTO.email} already exists" }
      return null
    }

    val authUser = authUserRepository.save(
      authenticationHandler.hashRegistrationPassword(registerDTO)
    )
    return AuthUserDTO.from(authUser)
  }
}
