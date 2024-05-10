package com.tul.backend.auth.base.service

import com.tul.backend.auth.base.dto.JwtClaims
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.github.oshai.kotlinlogging.KotlinLogging

class JwtService(
  secret: String
) {

  private val secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))

  fun generateToken(claims: JwtClaims): String {
    return Jwts
      .builder()
      .claims()
      .add(claims.claims)
      .issuedAt(claims.issuedAt)
      .expiration(claims.expiration)
      .and()
      .signWith(secretKey)
      .compact()
  }

  fun extractToken(token: String): Claims? {
    return try {
      Jwts
        .parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .payload
    } catch (e: Exception) {
      null
    }
  }
}
