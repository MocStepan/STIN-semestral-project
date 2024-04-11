package com.tul.backend.auth.service

import com.tul.backend.auth.base.valueobject.EmailAddress
import com.tul.backend.auth.dto.LoginDTO
import com.tul.backend.auth.dto.RegisterDTO
import com.tul.backend.auth.entity.AuthUser
import com.tul.backend.auth.repository.AuthUserRepository
import com.tul.backend.createAuthUser
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class AuthUserServiceTests : FeatureSpec({

  feature("login") {
    scenario("login with valid credentials") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val request = mockk<HttpServletRequest>()
      val loginDTO = LoginDTO(
        EmailAddress("test@test.cz"),
        "password"
      )

      every { spec.authenticationHandler.authenticate(loginDTO, request, response) } returns "token"

      val result = spec.authUserService.login(loginDTO, request, response)

      result shouldBe "token"
    }

    scenario("login with invalid email") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val request = mockk<HttpServletRequest>()
      val loginDTO = LoginDTO(
        EmailAddress("test@"),
        "password"
      )

      val result = spec.authUserService.login(loginDTO, request, response)

      result shouldBe null
      verify(exactly = 0) { spec.authenticationHandler.authenticate(any(), any(), any()) }
    }

    scenario("login with empty password") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val request = mockk<HttpServletRequest>()
      val loginDTO = LoginDTO(
        EmailAddress("test@test.cz"),
        ""
      )

      val result = spec.authUserService.login(loginDTO, request, response)

      result shouldBe null
      verify(exactly = 0) { spec.authenticationHandler.authenticate(any(), any(), any()) }
    }

    scenario("authenticate returns null") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val request = mockk<HttpServletRequest>()
      val loginDTO = LoginDTO(
        EmailAddress("test@test.cz"),
        "password"
      )

      every { spec.authenticationHandler.authenticate(loginDTO, request, response) } returns null

      val result = spec.authUserService.login(loginDTO, request, response)

      result shouldBe null
    }
  }

  feature("register") {
    scenario("register with valid credentials") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "username",
        EmailAddress("test@test.cz"),
        "password"
      )
      val authUser = createAuthUser()

      val authUserSlot = slot<AuthUser>()

      every { spec.authUserRepository.existsByEmail(registerDTO.email.value) } returns false
      every { spec.authenticationHandler.hashRegistrationPassword(registerDTO) } returns authUser
      every { spec.authUserRepository.save(capture(authUserSlot)) } returnsArgument 0

      val result = spec.authUserService.register(registerDTO)!!

      val captured = authUserSlot.captured
      result.id shouldBe captured.id
      result.username shouldBe captured.username
      result.email shouldBe captured.email
    }

    scenario("register with invalid email") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "username",
        EmailAddress("test@"),
        "password"
      )

      val result = spec.authUserService.register(registerDTO)

      result shouldBe null
      verify(exactly = 0) { spec.authenticationHandler.hashRegistrationPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.existsByEmail(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }

    scenario("register with invalid username") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "",
        EmailAddress("test@test.cz"),
        "password"
      )

      val result = spec.authUserService.register(registerDTO)

      result shouldBe null
      verify(exactly = 0) { spec.authenticationHandler.hashRegistrationPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.existsByEmail(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }

    scenario("register with invalid password") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "username",
        EmailAddress("test@test.cz"),
        ""
      )

      val result = spec.authUserService.register(registerDTO)

      result shouldBe null
      verify(exactly = 0) { spec.authenticationHandler.hashRegistrationPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.existsByEmail(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }

    scenario("register with existing email") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "username",
        EmailAddress("test@test.cz"),
        "password"
      )

      every { spec.authUserRepository.existsByEmail(registerDTO.email.value) } returns true


      val result = spec.authUserService.register(registerDTO)

      result shouldBe null
      verify(exactly = 0) { spec.authenticationHandler.hashRegistrationPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }
  }
})

private class AuthUserServiceSpecWrapper(
  val authenticationHandler: AuthenticationHandler,
  val authUserRepository: AuthUserRepository
) {
  val authUserService = AuthUserService(
    authenticationHandler,
    authUserRepository
  )
}

private fun getSpec() = AuthUserServiceSpecWrapper(mockk(), mockk())
