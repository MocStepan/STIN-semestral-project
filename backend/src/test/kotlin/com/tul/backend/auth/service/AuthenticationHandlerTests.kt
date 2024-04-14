package com.tul.backend.auth.service

import com.tul.backend.auth.base.service.CustomPasswordEncoder
import com.tul.backend.auth.base.service.CustomUserDetailsService
import com.tul.backend.auth.base.service.TokenService
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
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.User

class AuthenticationHandlerTests : FeatureSpec({

  feature("authenticate") {
    scenario("authenticate with valid credentials") {
      val spec = getSpec()
      val authUser = createAuthUser()
      val loginDTO = LoginDTO(
        authUser.email,
        authUser.password
      )
      val authToken = UsernamePasswordAuthenticationToken(
        loginDTO.email,
        loginDTO.password
      )
      val userDetails = User.builder()
        .username(authUser.email.value)
        .password(authUser.password)
        .roles(authUser.role.name)
        .build()
      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()


      every { spec.authManager.authenticate(authToken) } returnsArgument 0
      every { spec.customUserDetailsService.loadUserByUsername(loginDTO.email.value) } returns userDetails
      every { spec.customPasswordEncoder.matches(loginDTO.password, userDetails.password) } returns true
      every { spec.tokenService.generateAccessToken(userDetails) } returns "token"
      every { spec.tokenService.updateContext("token", request, response) } just runs

      val result = spec.authenticationHandler.authenticate(loginDTO, request, response)

      result shouldBe true
    }

    scenario("authenticate with invalid password") {
      val spec = getSpec()
      val authUser = createAuthUser()
      val loginDTO = LoginDTO(
        authUser.email,
        authUser.password
      )
      val authToken = UsernamePasswordAuthenticationToken(
        loginDTO.email,
        loginDTO.password
      )
      val userDetails = User.builder()
        .username(authUser.email.value)
        .password(authUser.password)
        .roles(authUser.role.name)
        .build()
      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()


      every { spec.authManager.authenticate(authToken) } returnsArgument 0
      every { spec.customUserDetailsService.loadUserByUsername(loginDTO.email.value) } returns userDetails
      every { spec.customPasswordEncoder.matches(loginDTO.password, userDetails.password) } returns false

      val result = spec.authenticationHandler.authenticate(loginDTO, request, response)

      result shouldBe false
      verify(exactly = 0) { spec.tokenService.generateAccessToken(userDetails) }
      verify(exactly = 0) { spec.tokenService.updateContext("token", request, response) }
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
  val authManager: AuthenticationManager,
  val customUserDetailsService: CustomUserDetailsService,
  val tokenService: TokenService,
  val customPasswordEncoder: CustomPasswordEncoder
) {
  val authenticationHandler = AuthenticationHandler(
    authManager,
    customUserDetailsService,
    tokenService,
    customPasswordEncoder
  )
}

private fun getSpec() = AuthenticationHandlerSpecWrapper(mockk(), mockk(), mockk(), mockk())
