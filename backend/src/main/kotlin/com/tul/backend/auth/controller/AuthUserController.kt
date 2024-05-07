package com.tul.backend.auth.controller

import com.tul.backend.auth.dto.SignInDTO
import com.tul.backend.auth.dto.SignUpDTO
import com.tul.backend.auth.service.AuthUserService
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

  @PostMapping("/v1/auth/signIn")
  fun signIn(
    @RequestBody signInDTO: SignInDTO,
    response: HttpServletResponse
  ): ResponseEntity<Boolean> {
    val responseDTO = authUserService.signIn(signInDTO, response)
    val status = if (responseDTO) HttpStatus.OK else HttpStatus.NOT_FOUND
    return ResponseEntity(responseDTO, status)
  }

  @PostMapping("/v1/auth/signUp")
  fun signUp(
    @RequestBody signUpDTO: SignUpDTO,
  ): ResponseEntity<Boolean> {
    val response = authUserService.signUp(signUpDTO)
    val status = if (response) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(response, status)
  }

  @PostMapping("/v1/auth/signOut")
  fun signOut(
    response: HttpServletResponse
  ): ResponseEntity<Boolean> {
    val responseDTO = authUserService.signOut(response)
    val status = if (responseDTO) HttpStatus.OK else HttpStatus.NOT_FOUND
    return ResponseEntity(responseDTO, status)
  }
}
