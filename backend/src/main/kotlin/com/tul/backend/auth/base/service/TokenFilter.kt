package com.tul.backend.auth.base.service

import com.tul.backend.auth.base.dto.AccessTokenClaims
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.web.util.WebUtils

@Component
class TokenFilter(
  private val accessTokenService: AccessTokenService
) {

  fun validateRequest(request: HttpServletRequest): AccessTokenClaims? {
    val token = WebUtils.getCookie(request, accessTokenService.COOKIE_NAME)

    if (token != null) {
      return accessTokenService.extractClaims(token.value)
    }
    return null
  }
}
