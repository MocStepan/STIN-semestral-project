package com.tul.backend.auth.controller

import com.tul.backend.auth.dto.AuthUserDTO
import com.tul.backend.auth.dto.LoginDTO
import com.tul.backend.auth.dto.RegisterDTO
import com.tul.backend.auth.service.AuthUserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
class AuthUserController(
    private val authUserService: AuthUserService,
) {

  @PostMapping("/auth/login")
  fun login(
      @RequestBody loginDTO: LoginDTO,
      request: HttpServletRequest,
      response: HttpServletResponse
  ): ResponseEntity<String?> {
    val responseDTO = authUserService.login(loginDTO, request, response)
    val status = if (responseDTO != null) HttpStatus.OK else HttpStatus.NOT_FOUND
    return ResponseEntity(responseDTO, status)
  }

  @PostMapping("/auth/register")
  fun register(
      @RequestBody registerDTO: RegisterDTO,
  ): ResponseEntity<AuthUserDTO?> {
    val response = authUserService.register(registerDTO)
    val status = if (response != null) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(response, status)
  }
}
