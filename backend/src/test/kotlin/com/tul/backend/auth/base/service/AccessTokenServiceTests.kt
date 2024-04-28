package com.tul.backend.auth.base.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.tul.backend.auth.base.dto.AccessTokenClaims
import com.tul.backend.createAuthUser
import com.tul.backend.objectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class AccessTokenServiceTests : FeatureSpec({

  feature("createCookie") {
    scenario("create cookie") {
      val spec = getSpec()
      val authUser = createAuthUser()
      val accessTokenClaims = AccessTokenClaims(authUser)

      val result = spec.accessTokenService.createCookie(accessTokenClaims)

      result.name shouldBe "access_token"
      result.value shouldNotBe null
      result.isHttpOnly shouldBe true
      result.path shouldBe "/"
      result.isSecure shouldBe true
      result.sameSite shouldBe "Lax"
      result.maxAge shouldNotBe null
    }
  }

  feature("extractClaims") {
    xscenario("create and extract cookie") {
      val spec = getSpec()
      val authUser = createAuthUser()
      val accessTokenClaims = AccessTokenClaims(authUser)

      val cookie = spec.accessTokenService.createCookie(accessTokenClaims)

      cookie.name shouldBe "access_token"
      cookie.isHttpOnly shouldBe true
      cookie.path shouldBe "/"
      cookie.isSecure shouldBe true
      cookie.sameSite shouldBe "Lax"

      val cookieValue = cookie.toString().split("access_token=")[1].split(";")[0]

      val result = spec.accessTokenService.extractClaims(cookieValue)

      result shouldBe accessTokenClaims
    }

    scenario("extract claims returns null, invalid jwt") {
      val spec = getSpec()

      val result = spec.accessTokenService.extractClaims("")

      result shouldBe null
    }
  }

  feature("createClaims") {
    scenario("create claims") {
      val spec = getSpec()
      val authUser = createAuthUser()

      val result = spec.accessTokenService.createClaims(authUser)

      result shouldBe AccessTokenClaims(authUser)
    }
  }
})

private class AccessTokenServiceSpecWrapper(
  objectMapper: ObjectMapper
) {
  val accessTokenService = AccessTokenService(
    objectMapper,
    true,
    "Lax",
    60000,
    "7A25432A462D4A614E645267556B58703272357538782F413F3328472B4B6250"
  )
}

private fun getSpec() = AccessTokenServiceSpecWrapper(objectMapper)
