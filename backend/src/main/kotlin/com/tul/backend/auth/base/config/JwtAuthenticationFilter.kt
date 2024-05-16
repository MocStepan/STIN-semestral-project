package com.tul.backend.auth.base.config

import com.tul.backend.auth.base.service.TokenFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
  private val tokenFilter: TokenFilter
) : OncePerRequestFilter() {

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    val validClaims = tokenFilter.validateRequest(request)

    if (validClaims != null) {
      val authToken = UsernamePasswordAuthenticationToken(validClaims, null, listOf(validClaims.authUserRole))
      SecurityContextHolder.getContext().authentication = authToken
    }

    filterChain.doFilter(request, response)
  }
}
