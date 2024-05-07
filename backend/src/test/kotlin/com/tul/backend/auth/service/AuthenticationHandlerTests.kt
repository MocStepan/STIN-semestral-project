package com.tul.backend.auth.service

import com.tul.backend.auth.base.dto.AccessTokenClaims
import com.tul.backend.auth.base.service.AccessTokenService
import com.tul.backend.auth.base.service.CustomPasswordEncoder
import com.tul.backend.auth.dto.SignInDTO
import com.tul.backend.auth.dto.SignUpDTO
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
      val signInDTO = SignInDTO(
        authUser.email,
        authUser.password
      )
      val cookie = ResponseCookie.from("access_token", "token").build()
      val response = mockk<HttpServletResponse>()

      every { spec.customPasswordEncoder.matches(signInDTO.password, authUser.password) } returns true
      every { spec.accessTokenService.createClaims(authUser) } returns claims
      every { spec.accessTokenService.createCookie(claims) } returns cookie
      every { response.addHeader("Set-Cookie", cookie.toString()) } just runs

      val result = spec.authenticationHandler.authenticate(signInDTO, authUser, response)

      result shouldBe true
    }

    scenario("authenticate with invalid password") {
      val spec = getSpec()
      val authUser = createAuthUser()
      val claims = AccessTokenClaims(authUser)
      val signInDTO = SignInDTO(
        authUser.email,
        authUser.password
      )
      val cookie = ResponseCookie.from("access_token", "token").build()
      val response = mockk<HttpServletResponse>()


      every { spec.customPasswordEncoder.matches(signInDTO.password, authUser.password) } returns false

      val result = spec.authenticationHandler.authenticate(signInDTO, authUser, response)

      result shouldBe false
      verify(exactly = 0) { spec.accessTokenService.createClaims(authUser) }
      verify(exactly = 0) { spec.accessTokenService.createCookie(claims) }
      verify(exactly = 0) { response.addHeader("Set-Cookie", cookie.toString()) }
    }
  }

  feature("hashSignUpPassword") {
    scenario("hash signUpDTO password") {
      val spec = getSpec()
      val auth = createAuthUser()
      val signUpDTO = SignUpDTO(
        auth.username,
        auth.email,
        auth.password,
        auth.password
      )

      every { spec.customPasswordEncoder.encode(signUpDTO.password) } returns "hashedPassword"

      val result = spec.authenticationHandler.hashSignUpPassword(signUpDTO)

      result.password shouldBe "hashedPassword"
    }
  }

  feature("signOut") {
    scenario("sign out") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val cookie = ResponseCookie.from("access_token", "").build()

      every { spec.accessTokenService.createEmptyCookie() } returns cookie
      every { response.addHeader("Set-Cookie", cookie.toString()) } just runs

      val result = spec.authenticationHandler.signOut(response)

      result shouldBe true
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
