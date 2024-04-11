package com.tul.backend.auth.service

import com.tul.backend.auth.base.service.CustomPasswordEncoder
import com.tul.backend.auth.base.service.TokenService
import com.tul.backend.auth.dto.LoginDTO
import com.tul.backend.auth.dto.RegisterDTO
import com.tul.backend.auth.entity.AuthUser
import com.tul.backend.auth.repository.AuthUserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
@Transactional
class AuthenticationHandler(
  private val authManager: AuthenticationManager,
  @Qualifier("customUserDetailsService") private val userDetailsService: UserDetailsService,
  private val tokenService: TokenService,
  private val customPasswordEncoder: CustomPasswordEncoder,
  private val authUserRepository: AuthUserRepository
) {
  fun authenticate(
    loginDTO: LoginDTO,
    request: HttpServletRequest,
    response: HttpServletResponse
  ): String? {
    authManager.authenticate(
      UsernamePasswordAuthenticationToken(
        loginDTO.email,
        loginDTO.password
      )
    )

    val userDetails = userDetailsService.loadUserByUsername(loginDTO.email.value)
    return if (customPasswordEncoder.matches(loginDTO.password, userDetails.password)) {
      val token = tokenService.generateAccessToken(userDetails)
      tokenService.updateContext(token, request, response)
      token
    } else {
      null
    }
  }

  fun hashRegistrationPassword(registerDTO: RegisterDTO): AuthUser {
    val authUser = AuthUser.from(registerDTO)
    authUser.password = customPasswordEncoder.encode(authUser.password)
    return authUser
  }
}
