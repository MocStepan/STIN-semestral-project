package com.tul.backend.auth.controller

import com.tul.backend.auth.base.valueobject.EmailAddress
import com.tul.backend.auth.dto.SignInDTO
import com.tul.backend.auth.dto.SignUpDTO
import com.tul.backend.auth.service.AuthUserService
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

class AuthUserControllerTests : FeatureSpec({

  feature("signIn") {
    scenario("sign in with valid credentials") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val signInDTO = SignInDTO(
        EmailAddress("email@email.cz"),
        "password"
      )

      every { spec.authUserService.signIn(signInDTO, response) } returns true

      val result = spec.authUserController.signIn(signInDTO, response)

      result.statusCode shouldBe HttpStatus.OK
      result.body shouldBe true
    }

    scenario("sign in with invalid credentials") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val signInDTO = SignInDTO(
        EmailAddress("email"),
        "password"
      )

      every { spec.authUserService.signIn(signInDTO, response) } returns false

      val result = spec.authUserController.signIn(signInDTO, response)

      result.statusCode shouldBe HttpStatus.NOT_FOUND
      result.body shouldBe false
    }
  }

  feature("signUp") {
    scenario("sign up with valid credentials") {
      val spec = getSpec()
      val signUpDTO = SignUpDTO(
        "username",
        EmailAddress("email"),
        "password",
        "password"
      )

      every { spec.authUserService.signUp(signUpDTO) } returns true

      val result = spec.authUserController.signUp(signUpDTO)

      result.statusCode shouldBe HttpStatus.OK
      result.body shouldBe true
    }

    scenario("sign up with invalid credentials") {
      val spec = getSpec()
      val signUpDTO = SignUpDTO(
        "username",
        EmailAddress("email"),
        "password",
        "password"
      )

      every { spec.authUserService.signUp(signUpDTO) } returns false

      val result = spec.authUserController.signUp(signUpDTO)

      result.statusCode shouldBe HttpStatus.BAD_REQUEST
      result.body shouldBe false
    }
  }
})

private class AuthUserControllerSpecWrapper(
  val authUserService: AuthUserService
) {
  val authUserController = AuthUserController(
    authUserService
  )
}

private fun getSpec() = AuthUserControllerSpecWrapper(mockk())