package com.tul.backend.auth.base.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.tul.backend.auth.base.dto.AccessTokenClaims
import com.tul.backend.auth.base.dto.JwtClaims
import com.tul.backend.auth.entity.AuthUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.milliseconds

@Service
class AccessTokenService(
  private val objectMapper: ObjectMapper,
  @Value("\${spring.jwt.secure}") private val secure: Boolean,
  @Value("\${spring.jwt.sameSite}") private val sameSite: String,
  @Value("\${spring.jwt.duration}") private val duration: Long,
  @Value("\${spring.jwt.secret}") private val secret: String
) {

  val COOKIE_NAME = "access_token"

  private val maxAge = duration.milliseconds

  private val jwtService = JwtService(secret)

  fun createCookie(accessTokenClaims: AccessTokenClaims): ResponseCookie {
    val claims = objectMapper.convertValue(accessTokenClaims, object : TypeReference<Map<String, *>>() {})
    val jwtToken = jwtService.generateToken(JwtClaims.from(claims, maxAge))
    return ResponseCookie.from(COOKIE_NAME, jwtToken)
      .httpOnly(true)
      .path("/")
      .secure(secure)
      .sameSite(sameSite)
      .maxAge(maxAge.inWholeSeconds)
      .build()
  }

  fun extractClaims(token: String): AccessTokenClaims? {
    return jwtService.extractToken(token)?.let {
      objectMapper.convertValue(it, object : TypeReference<AccessTokenClaims>() {})
    }
  }

  fun createClaims(authUser: AuthUser): AccessTokenClaims {
    return AccessTokenClaims(authUser)
  }

  fun createEmptyCookie(): ResponseCookie {
    return ResponseCookie.from(COOKIE_NAME, "")
      .httpOnly(true)
      .path("/")
      .secure(secure)
      .sameSite(sameSite)
      .maxAge(0)
      .build()
  }
}
