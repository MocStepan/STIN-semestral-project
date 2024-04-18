package com.tul.backend.auth.service

import com.tul.backend.auth.base.service.CustomPasswordEncoder
import com.tul.backend.auth.base.service.TokenService
import com.tul.backend.auth.dto.LoginDTO
import com.tul.backend.auth.dto.RegisterDTO
import com.tul.backend.auth.entity.AuthUser
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthenticationHandler(
  private val authManager: AuthenticationManager,
  @Qualifier("customUserDetailsService") private val userDetailsService: UserDetailsService,
  private val tokenService: TokenService,
  private val customPasswordEncoder: CustomPasswordEncoder
) {
  fun authenticate(
    loginDTO: LoginDTO,
    request: HttpServletRequest,
    response: HttpServletResponse
  ): Boolean {
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
      true
    } else {
      false
    }
  }

  fun hashRegistrationPassword(registerDTO: RegisterDTO): AuthUser {
    val authUser = AuthUser.from(registerDTO)
    authUser.password = customPasswordEncoder.encode(authUser.password)
    return authUser
  }
}
