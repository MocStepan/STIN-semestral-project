package com.tul.backend.auth.base.service

import com.tul.backend.auth.base.dto.AccessTokenClaims
import com.tul.backend.createAuthUser
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest

class TokenFilterTests : FeatureSpec({

  feature("validateRequest") {

    scenario("validate request") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()
      val authUser = createAuthUser()
      val accessTokenClaims = AccessTokenClaims(authUser)

      every { request.cookies } returns arrayOf(Cookie("access_token", "token"))
      every { spec.accessTokenService.COOKIE_NAME } returns "access_token"
      every { spec.accessTokenService.extractClaims("token") } returns accessTokenClaims

      val result = spec.tokenFilter.validateRequest(request)

      result shouldBe accessTokenClaims
    }

    scenario("validate request with no token") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()

      every { request.cookies } returns null
      every { spec.accessTokenService.COOKIE_NAME } returns "access_token"

      val result = spec.tokenFilter.validateRequest(request)

      result shouldBe null
    }

    scenario("validate request with wrong cookie name") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()

      every { request.cookies } returns arrayOf(Cookie("other_cookie", "token"))
      every { spec.accessTokenService.COOKIE_NAME } returns "access_token"

      val result = spec.tokenFilter.validateRequest(request)

      result shouldBe null
    }

    scenario("validate request with invalid jwt claims") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()

      every { request.cookies } returns arrayOf(Cookie("access_token", "token"))
      every { spec.accessTokenService.COOKIE_NAME } returns "access_token"
      every { spec.accessTokenService.extractClaims("token") } returns null

      val result = spec.tokenFilter.validateRequest(request)

      result shouldBe null
    }
  }
})

private class TokenFilterSpecWrapper(
  val accessTokenService: AccessTokenService
) {
  val tokenFilter = TokenFilter(accessTokenService)
}

private fun getSpec() = TokenFilterSpecWrapper(mockk())
