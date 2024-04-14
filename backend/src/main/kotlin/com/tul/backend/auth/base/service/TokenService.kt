package com.tul.backend.auth.base.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TokenService(
  @Value("\${spring.jwt.secure}") private val secure: Boolean,
  @Value("\${spring.jwt.sameSite}") private val sameSite: String,
  @Value("\${spring.jwt.duration}") private val duration: Long,
  @Value("\${spring.jwt.secret}") private val secret: String
) {

  private val secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))

  fun generateAccessToken(
    userDetails: UserDetails,
    additionalClaims: Map<String, Any> = emptyMap()
  ): String {
    return Jwts.builder()
      .claims()
      .subject(userDetails.username)
      .issuedAt(Date(System.currentTimeMillis()))
      .expiration(Date(System.currentTimeMillis() + duration))
      .add(additionalClaims)
      .and()
      .signWith(secretKey)
      .compact()
  }

  fun extractEmail(token: String): String? =
    getAllClaims(token).subject

  fun isTokenExpired(token: String): Boolean =
    getAllClaims(token).expiration.before(Date(System.currentTimeMillis()))

  private fun getAllClaims(token: String): Claims {
    val parser = Jwts.parser()
      .verifyWith(secretKey)
      .build()

    return parser.parseSignedClaims(token).payload
  }

  fun isValidToken(token: String?, userDetails: UserDetails): Boolean {
    val username: String? = extractEmail(token!!)
    return username == userDetails.username && !isTokenExpired(token)
  }

  fun updateContext(
    jwtToken: String,
    request: HttpServletRequest,
    response: HttpServletResponse
  ) {
    val cookie = ResponseCookie.from("access_token", jwtToken)
      .httpOnly(true)
      .path("/")
      .secure(secure)
      .sameSite(sameSite)
      .maxAge(duration)
      .build()
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
  }

  fun clearCookies(response: HttpServletResponse) {
    val cookie = ResponseCookie.from("access_token", "")
      .httpOnly(true)
      .path("/")
      .secure(secure)
      .sameSite(sameSite)
      .maxAge(duration)
      .build()
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
  }
}
