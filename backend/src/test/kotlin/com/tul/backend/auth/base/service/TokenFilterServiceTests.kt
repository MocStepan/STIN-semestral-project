package com.tul.backend.auth.base.service

import com.tul.backend.auth.base.valueobject.AuthUserRole
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.userdetails.User

class TokenFilterServiceTests : FeatureSpec({

  feature("validateRequest") {

    scenario("validate request") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()
      val userDetails = User.builder()
        .username("email")
        .password("password")
        .roles(AuthUserRole.ADMIN.name)
        .build()
      val securityContext = mockk<SecurityContext>()

      every { request.getHeader("Cookie") } returns "access_token=token"
      every { spec.tokenService.extractEmail("token") } returns "email"
      every { securityContext.authentication } returns null
      every { spec.userDetailsService.loadUserByUsername("email") } returns userDetails
      every { spec.tokenService.isValidToken("token", userDetails) } returns true

      val result = spec.tokenFilterService.validateRequest(request)

      result shouldBe userDetails
    }

    scenario("request without cookie header") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()

      every { request.getHeader("Cookie") } returns null

      val result = spec.tokenFilterService.validateRequest(request)

      result shouldBe null
    }

    scenario("validate request with null email") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()

      every { request.getHeader("Cookie") } returns "access_token=token"
      every { spec.tokenService.extractEmail("token") } returns null

      val result = spec.tokenFilterService.validateRequest(request)

      result shouldBe null
      verify(exactly = 0) { spec.userDetailsService.loadUserByUsername(any()) }
    }

    scenario("validate request with wrong token") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()

      every { request.getHeader("Cookie") } returns "dasd=token"

      val result = spec.tokenFilterService.validateRequest(request)

      result shouldBe null
      verify(exactly = 0) { spec.tokenService.extractEmail(any()) }
      verify(exactly = 0) { spec.userDetailsService.loadUserByUsername(any()) }
    }

    scenario("validate request with null authentication") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()
      val securityContext = mockk<SecurityContext>()

      every { request.getHeader("Cookie") } returns null
      every { spec.tokenService.extractEmail("token") } returns "email"
      every { securityContext.authentication } returns mockk()

      val result = spec.tokenFilterService.validateRequest(request)

      result shouldBe null
      verify(exactly = 0) { spec.userDetailsService.loadUserByUsername(any()) }
    }

    scenario("loadUserByUsername throws exception") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()

      every { request.getHeader("Cookie") } returns "access_token=token"
      every { spec.tokenService.extractEmail("token") } returns "email"
      every { spec.userDetailsService.loadUserByUsername("email") } throws Exception()

      shouldThrow<Exception> {
        spec.tokenFilterService.validateRequest(request)
      }
      verify(exactly = 0) { spec.tokenService.isValidToken(any(), any()) }
    }

    scenario("invalid token") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()
      val userDetails = User.builder()
        .username("email")
        .password("password")
        .roles(AuthUserRole.ADMIN.name)
        .build()

      every { request.getHeader("Cookie") } returns "access_token=token"
      every { spec.tokenService.extractEmail("token") } returns "email"
      every { spec.userDetailsService.loadUserByUsername("email") } returns userDetails
      every { spec.tokenService.isValidToken("token", userDetails) } returns false

      val result = spec.tokenFilterService.validateRequest(request)

      result shouldBe null
    }
  }

  feature("updateContext") {

    scenario("update context") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      val userDetails = User.builder()
        .username("email")
        .password("password")
        .roles(AuthUserRole.ADMIN.name)
        .build()
      val session = mockk<HttpSession>()

      every { request.remoteAddr } returns "remoteAddr"
      every { request.getSession(false) } returns session
      every { session.id } returns "sessionId"

      spec.tokenFilterService.updateContext(userDetails, request, response)
    }
  }
})

private class TokenFilterServiceSpecWrapper(
  val userDetailsService: CustomUserDetailsService,
  val tokenService: TokenService
) {
  val tokenFilterService = TokenFilterService(userDetailsService, tokenService)
}

private fun getSpec() = TokenFilterServiceSpecWrapper(mockk(), mockk())
