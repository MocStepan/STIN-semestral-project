package com.tul.backend.auth.base.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.util.WebUtils

@Component
class TokenFilterService(
  @Qualifier("customUserDetailsService") private val userDetailsService: UserDetailsService,
  private val tokenService: TokenService
) {

  fun validateRequest(request: HttpServletRequest, response: HttpServletResponse): UserDetails? {
    try {
      val (email, token) = extractEmailAndToken(request)

      if (email != null && SecurityContextHolder.getContext().authentication == null) {
        val userDetails = userDetailsService.loadUserByUsername(email)
        if (tokenService.isValidToken(token, userDetails)) {
          return userDetails
        }
      }
      return null
    } catch (e: Exception) {
      tokenService.clearCookies(response)
      return null
    }
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

    SecurityContextHolder.getContext().authentication = authToken
  }

  private fun extractEmailAndToken(request: HttpServletRequest): Pair<String?, String?> {
    val authHeader = WebUtils.getCookie(request, "access_token")
    var token: String? = null
    var email: String? = null

    if (authHeader != null && authHeader.name == "access_token") {
      token = authHeader.value
      email = tokenService.extractEmail(token)
    }
    return Pair(email, token)
  }
}
