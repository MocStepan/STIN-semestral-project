package com.tul.backend.auth.service

import com.tul.backend.auth.base.service.CustomPasswordEncoder
import com.tul.backend.auth.base.service.TokenService
import com.tul.backend.auth.dto.LoginDTO
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
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
  ): String? {
    authManager.authenticate(
        UsernamePasswordAuthenticationToken(
            loginDTO.email,
            loginDTO.password
        )
    )

    val userDetails = userDetailsService.loadUserByUsername(loginDTO.email)
    if (customPasswordEncoder.matches(loginDTO.password, userDetails.password)) {
      tokenService.updateContext(userDetails, request, response)
      return "success"
    } else {
      throw RuntimeException("Invalid credentials")
    }
  }
}
