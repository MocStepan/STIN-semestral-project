package com.tul.backend.auth.controller

import com.tul.backend.auth.base.valueobject.EmailAddress
import com.tul.backend.auth.dto.AuthUserDTO
import com.tul.backend.auth.dto.LoginDTO
import com.tul.backend.auth.dto.RegisterDTO
import com.tul.backend.auth.service.AuthUserService
import com.tul.backend.createAuthUser
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

class AuthUserControllerTests : FeatureSpec({

  feature("login") {
    scenario("login with valid credentials") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val request = mockk<HttpServletRequest>()
      val loginDTO = LoginDTO(
        EmailAddress("email@email.cz"),
        "password"
      )

      every { spec.authUserService.login(loginDTO, request, response) } returns true

      val result = spec.authUserController.login(loginDTO, request, response)

      result.statusCode shouldBe HttpStatus.OK
      result.body shouldBe true
    }

    scenario("login with invalid credentials") {
      val spec = getSpec()
      val response = mockk<HttpServletResponse>()
      val request = mockk<HttpServletRequest>()
      val loginDTO = LoginDTO(
        EmailAddress("email"),
        "password"
      )

      every { spec.authUserService.login(loginDTO, request, response) } returns false

      val result = spec.authUserController.login(loginDTO, request, response)

      result.statusCode shouldBe HttpStatus.NOT_FOUND
      result.body shouldBe false
    }
  }

  feature("register") {
    scenario("register with valid credentials") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "username",
        EmailAddress("email"),
        "password",
        "password"
      )
      val authUserDTO = AuthUserDTO.from(createAuthUser())

      every { spec.authUserService.register(registerDTO) } returns authUserDTO

      val result = spec.authUserController.register(registerDTO)

      result.statusCode shouldBe HttpStatus.OK
      result.body shouldBe authUserDTO
    }

    scenario("register with invalid credentials") {
      val spec = getSpec()
      val registerDTO = RegisterDTO(
        "username",
        EmailAddress("email"),
        "password",
        "password"
      )

      every { spec.authUserService.register(registerDTO) } returns null

      val result = spec.authUserController.register(registerDTO)

      result.statusCode shouldBe HttpStatus.BAD_REQUEST
      result.body shouldBe null
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