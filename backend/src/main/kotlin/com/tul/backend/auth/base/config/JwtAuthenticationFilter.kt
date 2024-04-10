package com.tul.backend.auth.base.config

import com.tul.backend.auth.base.service.TokenFilterService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
  private val tokenFilterService: TokenFilterService
) : OncePerRequestFilter() {
  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    val userDetails = tokenFilterService.validateRequest(request)
    if (userDetails != null) {
      tokenFilterService.updateContext(userDetails, request, response)
    }
    filterChain.doFilter(request, response)
  }
}