package com.tul.backend.auth.service

import com.tul.backend.auth.base.valueobject.EmailAddress
import com.tul.backend.auth.dto.SignInDTO
import com.tul.backend.auth.dto.SignUpDTO
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

  feature("signIn") {
    scenario("sign in with valid credentials") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val authUser = createAuthUser()
      val signInDTO = SignInDTO(
        authUser.email,
        authUser.password
      )

      every { spec.authenticationHandler.authenticate(signInDTO, authUser, response) } returns true
      every { spec.authUserRepository.findByEmail(authUser.email.value) } returns authUser

      val result = spec.authUserService.signIn(signInDTO, response)

      result shouldBe true
    }

    scenario("sign in with invalid email") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val authUser = createAuthUser()
      val signInDTO = SignInDTO(
        EmailAddress("test"),
        authUser.password
      )

      val result = spec.authUserService.signIn(signInDTO, response)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.authenticate(any(), any(), any()) }
    }

    scenario("sign in with empty password") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val signInDTO = SignInDTO(
        EmailAddress("test@test.cz"),
        ""
      )

      val result = spec.authUserService.signIn(signInDTO, response)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.authenticate(any(), any(), any()) }
    }

    scenario("authenticate returns false") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val authUser = createAuthUser()
      val signInDTO = SignInDTO(
        authUser.email,
        authUser.password
      )

      every { spec.authUserRepository.findByEmail(authUser.email.value) } returns authUser
      every { spec.authenticationHandler.authenticate(signInDTO, authUser, response) } returns false

      val result = spec.authUserService.signIn(signInDTO, response)

      result shouldBe false
    }

    scenario("email not found") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val authUser = createAuthUser()
      val signInDTO = SignInDTO(
        authUser.email,
        authUser.password
      )

      every { spec.authUserRepository.findByEmail(authUser.email.value) } returns null

      val result = spec.authUserService.signIn(signInDTO, response)

      result shouldBe false
    }
  }

  feature("signUp") {
    scenario("sign up with valid credentials") {
      val spec = getSpec()
      val signUpDTO = SignUpDTO(
        "username",
        EmailAddress("test@test.cz"),
        "password",
        "password"
      )
      val authUser = AuthUser.from(signUpDTO)

      val authUserSlot = slot<AuthUser>()

      every { spec.authUserRepository.existsByEmail(signUpDTO.email.value) } returns false
      every { spec.authenticationHandler.hashSignUpPassword(signUpDTO) } returns authUser
      every { spec.authUserRepository.save(capture(authUserSlot)) } returnsArgument 0

      val result = spec.authUserService.signUp(signUpDTO)

      val captured = authUserSlot.captured
      result shouldBe true
      captured.username shouldBe signUpDTO.username
      captured.email shouldBe signUpDTO.email
      captured.password shouldBe authUser.password
      signUpDTO.passwordConfirmation shouldBe "password"
    }

    scenario("sign up with invalid email") {
      val spec = getSpec()
      val signUpDTO = SignUpDTO(
        "username",
        EmailAddress("test@"),
        "password",
        "password"
      )

      val result = spec.authUserService.signUp(signUpDTO)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.hashSignUpPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.existsByEmail(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }

    scenario("sign up with invalid username") {
      val spec = getSpec()
      val signUpDTO = SignUpDTO(
        "",
        EmailAddress("test@test.cz"),
        "password",
        "password"
      )

      val result = spec.authUserService.signUp(signUpDTO)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.hashSignUpPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.existsByEmail(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }

    scenario("sign up with invalid password") {
      val spec = getSpec()
      val signUpDTO = SignUpDTO(
        "username",
        EmailAddress("test@test.cz"),
        "",
        ""
      )

      val result = spec.authUserService.signUp(signUpDTO)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.hashSignUpPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.existsByEmail(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }

    scenario("sign up with existing email") {
      val spec = getSpec()
      val signUpDTO = SignUpDTO(
        "username",
        EmailAddress("test@test.cz"),
        "password",
        "password"
      )

      every { spec.authUserRepository.existsByEmail(signUpDTO.email.value) } returns true


      val result = spec.authUserService.signUp(signUpDTO)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.hashSignUpPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }

    scenario("sign up with not same passwords") {
      val spec = getSpec()
      val signUpDTO = SignUpDTO(
        "username",
        EmailAddress("test@test.cz"),
        "password",
        "password123"
      )

      val result = spec.authUserService.signUp(signUpDTO)

      result shouldBe false
      verify(exactly = 0) { spec.authenticationHandler.hashSignUpPassword(any()) }
      verify(exactly = 0) { spec.authUserRepository.existsByEmail(any()) }
      verify(exactly = 0) { spec.authUserRepository.save(any()) }
    }
  }

  feature("signOut") {
    scenario("sign out") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()

      every { spec.authenticationHandler.signOut(response) } returns true

      val result = spec.authUserService.signOut(response)

      result shouldBe true
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
