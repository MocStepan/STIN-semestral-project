package com.tul.backend.auth.controller

import com.tul.backend.auth.dto.AuthUserDTO
import com.tul.backend.auth.dto.LoginDTO
import com.tul.backend.auth.dto.RegisterDTO
import com.tul.backend.auth.service.AuthUserService
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
    @PostMapping("/login")
    fun login(
        @RequestBody loginDTO: LoginDTO,
    ): ResponseEntity<AuthUserDTO?> {
        val response = authUserService.login(loginDTO)
        val status = if (response != null) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity(response, status)
    }

    @PostMapping("/logout")
    fun logout(
        @RequestBody userId: Long,
    ): ResponseEntity<Boolean> {
        val response = authUserService.logout(userId)
        val status = if (response) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity(response, status)
    }

    @PostMapping("/register")
    fun register(
        @RequestBody registerDTO: RegisterDTO,
    ): ResponseEntity<AuthUserDTO?> {
        val response = authUserService.register(registerDTO)
        val status = if (response != null) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity(response, status)
    }
}
