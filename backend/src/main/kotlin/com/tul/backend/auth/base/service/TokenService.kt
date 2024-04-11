package com.tul.backend.auth.base.service

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TokenService(
  @Value("\${jwt.key}") private val key: String,
  @Value("\${jwt.access-token-expiration}") private val expirationDate: Int
) {

  private val secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key))

  fun generateAccessToken(
    userDetails: UserDetails,
    additionalClaims: Map<String, Any> = emptyMap()
  ): String {
    return Jwts.builder()
      .claims()
      .subject(userDetails.username)
      .issuedAt(Date(System.currentTimeMillis()))
      .expiration(Date(System.currentTimeMillis() + expirationDate))
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
    foundAuthUser: UserDetails,
    request: HttpServletRequest,
    response: HttpServletResponse
  ) {
    val jwtToken = generateAccessToken(foundAuthUser)
    val cookie = Cookie("access_token", jwtToken)
    cookie.maxAge = expirationDate
    cookie.path = "/"
    response.addCookie(cookie)
  }
}
