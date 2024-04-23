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
import jakarta.servlet.http.HttpServletResponse

class AuthUserServiceTests : FeatureSpec({

  feature("login") {
    scenario("login with valid credentials") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val authUser = createAuthUser()
      val loginDTO = LoginDTO(
        authUser.email,
        authUser.password
      )

      every { spec.authenticationHandler.authenticate(loginDTO, authUser, response) } returns true
      every { spec.authUserRepository.findByEmail(authUser.email.value) } returns authUser

      val result = spec.authUserService.login(loginDTO, response)

      result shouldBe true
    }

    scenario("login with invalid email") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val authUser = createAuthUser()
      val loginDTO = LoginDTO(
        EmailAddress("test"),
        authUser.password
      )

      val result = spec.authUserService.login(loginDTO, response)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.authenticate(any(), any(), any()) }
    }

    scenario("login with empty password") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val loginDTO = LoginDTO(
        EmailAddress("test@test.cz"),
        ""
      )

      val result = spec.authUserService.login(loginDTO, response)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.authenticate(any(), any(), any()) }
    }

    scenario("authenticate returns false") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val authUser = createAuthUser()
      val loginDTO = LoginDTO(
        authUser.email,
        authUser.password
      )

      every { spec.authUserRepository.findByEmail(authUser.email.value) } returns authUser
      every { spec.authenticationHandler.authenticate(loginDTO, authUser, response) } returns false

      val result = spec.authUserService.login(loginDTO, response)

      result shouldBe false
    }

    scenario("email not found") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val authUser = createAuthUser()
      val loginDTO = LoginDTO(
        authUser.email,
        authUser.password
      )

      every { spec.authUserRepository.findByEmail(authUser.email.value) } returns null

      val result = spec.authUserService.login(loginDTO, response)

      result shouldBe false
    }
  }

  feature("register") {
    scenario("register with valid credentials") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "username",
        EmailAddress("test@test.cz"),
        "password",
        "password"
      )
      val authUser = AuthUser.from(registerDTO)

      val authUserSlot = slot<AuthUser>()

      every { spec.authUserRepository.existsByEmail(registerDTO.email.value) } returns false
      every { spec.authenticationHandler.hashRegistrationPassword(registerDTO) } returns authUser
      every { spec.authUserRepository.save(capture(authUserSlot)) } returnsArgument 0

      val result = spec.authUserService.register(registerDTO)

      val captured = authUserSlot.captured
      result shouldBe true
      captured.username shouldBe registerDTO.username
      captured.email shouldBe registerDTO.email
      captured.password shouldBe authUser.password
      registerDTO.passwordConfirmation shouldBe "password"
    }

    scenario("register with invalid email") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "username",
        EmailAddress("test@"),
        "password",
        "password"
      )

      val result = spec.authUserService.register(registerDTO)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.hashRegistrationPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.existsByEmail(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }

    scenario("register with invalid username") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "",
        EmailAddress("test@test.cz"),
        "password",
        "password"
      )

      val result = spec.authUserService.register(registerDTO)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.hashRegistrationPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.existsByEmail(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }

    scenario("register with invalid password") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "username",
        EmailAddress("test@test.cz"),
        "",
        ""
      )

      val result = spec.authUserService.register(registerDTO)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.hashRegistrationPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.existsByEmail(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }

    scenario("register with existing email") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "username",
        EmailAddress("test@test.cz"),
        "password",
        "password"
      )

      every { spec.authUserRepository.existsByEmail(registerDTO.email.value) } returns true


      val result = spec.authUserService.register(registerDTO)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.hashRegistrationPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }

    scenario("register with not same passwords") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "username",
        EmailAddress("test@test.cz"),
        "password",
        "password123"
      )

      val result = spec.authUserService.register(registerDTO)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.hashRegistrationPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.existsByEmail(any()) }
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
