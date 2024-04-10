package com.tul.backend.auth.service

import com.tul.backend.auth.dto.AuthUserDTO
import com.tul.backend.auth.dto.LoginDTO
import com.tul.backend.auth.dto.RegisterDTO
import com.tul.backend.auth.entity.AuthUser
import com.tul.backend.auth.repository.AuthUserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
@Transactional
class AuthUserService(
  private val authUserRepository: AuthUserRepository,
  private val authenticationHandler: AuthenticationHandler

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

    val authUser = AuthUser.from(registerDTO)
    val savedAuthUser = authUserRepository.save(authUser)
    return AuthUserDTO.from(savedAuthUser)
  }

  fun logout(userId: Long): Boolean {
    val authUser = authUserRepository.findByIdOrNull(userId)
    if (authUser == null) {
      log.warn { "AuthUser with id: $userId is not found" }
      return true
    }

    return false
  }
}
