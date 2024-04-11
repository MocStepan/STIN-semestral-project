package com.tul.backend.auth.base.service

import com.tul.backend.auth.base.valueobject.AuthUserRole
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.config.annotation.rsocket.RSocketSecurity.JwtSpec
import org.springframework.security.core.userdetails.User

class TokenServiceTests : FeatureSpec({

  val userDetails = User.builder()
      .username("test@test.cz")
      .password("password")
      .roles(AuthUserRole.ADMIN.name)
      .build()

  feature("generateAccessToken") {
    scenario("generate access token") {
      val spec = getSpec()

      spec.tokenService.generateAccessToken(userDetails)
    }
  }

  feature("extractEmail") {
    scenario("extract email") {
      val spec = getSpec()

      val token = spec.tokenService.generateAccessToken(userDetails)

      val result = spec.tokenService.extractEmail(token)

      result shouldBe userDetails.username
    }

    scenario("extract email with invalid token") {
      val spec = getSpec()

      shouldThrow<Exception> {
        spec.tokenService.extractEmail("invalid token")
      }
    }
  }

  feature("isTokenExpired") {
    scenario("token is not expired") {
      val spec = getSpec()

      val token = spec.tokenService.generateAccessToken(userDetails)

      val result = spec.tokenService.isTokenExpired(token)

      result shouldBe false
    }

    scenario("token is expired") {
      val tokenService = TokenService("7A25432A462D4A614E645267556B58703272357538782F413F3328472B4B6250", 0)

      val expiredUser = User.builder()
          .username("test@test.cz")
          .password("password")
          .roles(AuthUserRole.ADMIN.name)
          .build()

      val token = tokenService.generateAccessToken(expiredUser)

      shouldThrow<ExpiredJwtException> {
        tokenService.isTokenExpired(token)
      }
    }
  }

  feature("isValidToken") {

    scenario("valid token") {
      val spec = getSpec()

      val token = spec.tokenService.generateAccessToken(userDetails)

      val result = spec.tokenService.isValidToken(token, userDetails)

      result shouldBe true
    }

    scenario("invalid token") {
      val spec = getSpec()
      val token = spec.tokenService.generateAccessToken(userDetails)
      val invalidUserDetails = User.builder()
          .username("admin@admin.cz")
          .password("admin")
          .roles(AuthUserRole.ADMIN.name)
          .build()

      val result = spec.tokenService.isValidToken(token, invalidUserDetails)

      result shouldBe false
    }

    scenario("token is null") {
      val spec = getSpec()

      shouldThrow<NullPointerException> {
        spec.tokenService.isValidToken(null, userDetails)
      }
    }

    scenario("token is expired") {
      val spec = getSpec()
      val mockkTokenService = mockk<TokenService>()
      val token = spec.tokenService.generateAccessToken(userDetails)

      every { mockkTokenService.isTokenExpired(token) } returns true

      val result = spec.tokenService.isValidToken(token, userDetails)

      result shouldBe true
    }
  }

  feature("updateContext") {
    scenario("update context") {
      val spec = getSpec()

      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()

      every { response.addCookie(any()) } just runs

      spec.tokenService.updateContext(userDetails, request, response)
    }
  }
})

private class TokenServiceSpecWrapper {
  val tokenService = TokenService("7A25432A462D4A614E645267556B58703272357538782F413F3328472B4B6250", 6000)
}

private fun getSpec() = TokenServiceSpecWrapper()
