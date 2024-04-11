package com.tul.backend.auth.base.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component

@Component
class TokenFilterService(
    @Qualifier("customUserDetailsService") private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService
) {

  fun validateRequest(request: HttpServletRequest): UserDetails? {
    val (email, token) = extractEmailAndToken(request)

    if (email != null && SecurityContextHolder.getContext().authentication == null) {
      val userDetails = userDetailsService.loadUserByUsername(email)
      if (tokenService.isValidToken(token, userDetails)) {
        return userDetails
      }
    }
    return null
  }

  fun updateContext(
      userDetails: UserDetails,
      request: HttpServletRequest,
      response: HttpServletResponse,
  ) {
    val authToken = UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.authorities
    )
    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

    SecurityContextHolder.getContext().authentication = authToken
    tokenService.updateContext(userDetails, request, response)
  }

  private fun extractEmailAndToken(request: HttpServletRequest): Pair<String?, String?> {
    val authHeader = request.getHeader("Cookie")
    var token: String? = null
    var email: String? = null

    if (authHeader != null && authHeader.startsWith("access_token=")) {
      token = authHeader.substring(13)
      email = tokenService.extractEmail(token)
    }
    return Pair(email, token)
  }
}
