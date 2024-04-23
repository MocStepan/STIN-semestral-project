package com.tul.backend.auth.service

import com.tul.backend.auth.base.dto.AccessTokenClaims
import com.tul.backend.auth.base.service.AccessTokenService
import com.tul.backend.auth.base.service.CustomPasswordEncoder
import com.tul.backend.auth.dto.LoginDTO
import com.tul.backend.auth.dto.RegisterDTO
import com.tul.backend.createAuthUser
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie

class AuthenticationHandlerTests : FeatureSpec({

  feature("authenticate") {
    scenario("authenticate with valid credentials") {
      val spec = getSpec()
      val authUser = createAuthUser()
      val claims = AccessTokenClaims(authUser)
      val loginDTO = LoginDTO(
        authUser.email,
        authUser.password
      )
      val cookie = ResponseCookie.from("access_token", "token").build()
      val response = mockk<HttpServletResponse>()

      every { spec.customPasswordEncoder.matches(loginDTO.password, authUser.password) } returns true
      every { spec.accessTokenService.createClaims(authUser) } returns claims
      every { spec.accessTokenService.createCookie(claims) } returns cookie
      every { response.addHeader("Set-Cookie", cookie.toString()) } just runs

      val result = spec.authenticationHandler.authenticate(loginDTO, authUser, response)

      result shouldBe true
    }

    scenario("authenticate with invalid password") {
      val spec = getSpec()
      val authUser = createAuthUser()
      val claims = AccessTokenClaims(authUser)
      val loginDTO = LoginDTO(
        authUser.email,
        authUser.password
      )
      val cookie = ResponseCookie.from("access_token", "token").build()
      val response = mockk<HttpServletResponse>()


      every { spec.customPasswordEncoder.matches(loginDTO.password, authUser.password) } returns false

      val result = spec.authenticationHandler.authenticate(loginDTO, authUser, response)

      result shouldBe false
      verify(exactly = 0) { spec.accessTokenService.createClaims(authUser) }
      verify(exactly = 0) { spec.accessTokenService.createCookie(claims) }
      verify(exactly = 0) { response.addHeader("Set-Cookie", cookie.toString()) }
    }
  }

  feature("hashRegistrationPassword") {
    scenario("hash registration password") {
      val spec = getSpec()
      val auth = createAuthUser()
      val registerDTO = RegisterDTO(
        auth.username,
        auth.email,
        auth.password,
        auth.password
      )

      every { spec.customPasswordEncoder.encode(registerDTO.password) } returns "hashedPassword"

      val result = spec.authenticationHandler.hashRegistrationPassword(registerDTO)

      result.password shouldBe "hashedPassword"
    }
  }
})

private class AuthenticationHandlerSpecWrapper(
  val accessTokenService: AccessTokenService,
  val customPasswordEncoder: CustomPasswordEncoder
) {
  val authenticationHandler = AuthenticationHandler(
    accessTokenService,
    customPasswordEncoder
  )
}

private fun getSpec() = AuthenticationHandlerSpecWrapper(mockk(), mockk())
