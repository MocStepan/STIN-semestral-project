package com.tul.backend.auth.base.dto

import java.util.Date
import kotlin.time.Duration

class JwtClaims(
  val claims: Map<String, *>,
  val issuedAt: Date,
  val expiration: Date
) {

  companion object {
    fun from(claims: Map<String, *>, duration: Duration): JwtClaims {
      val now = Date()
      val expiration = Date(now.time + duration.inWholeSeconds)

      return JwtClaims(
        claims = claims,
        issuedAt = now,
        expiration = expiration
      )
    }
  }
}