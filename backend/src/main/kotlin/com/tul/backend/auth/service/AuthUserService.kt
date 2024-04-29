package com.tul.backend.auth.service

import com.tul.backend.auth.dto.SignInDTO
import com.tul.backend.auth.dto.SignUpDTO
import com.tul.backend.auth.repository.AuthUserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
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
  fun signIn(signInDTO: SignInDTO, response: HttpServletResponse): Boolean {
    if (!signInDTO.isValid()) {
      log.warn { "SignInDTO: $signInDTO is invalid" }
      return false
    }

    val authUser = authUserRepository.findByEmail(signInDTO.email.value)
    if (authUser == null) {
      log.warn { "User with email: ${signInDTO.email} does not exist" }
      return false
    }

    return authenticationHandler.authenticate(signInDTO, authUser, response)
  }

  fun signUp(signUpDTO: SignUpDTO): Boolean {
    if (!signUpDTO.isValid()) {
      log.warn { "SignUpDTO: $signUpDTO is invalid" }
      return false
    }

    val exists = authUserRepository.existsByEmail(signUpDTO.email.value)
    if (exists) {
      log.warn { "User with email: ${signUpDTO.email} already exists" }
      return false
    }

    authUserRepository.save(
      authenticationHandler.hashSignUpPassword(signUpDTO)
    )
    return true
  }
}
