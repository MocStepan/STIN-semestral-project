package com.tul.backend.auth.service

import com.tul.backend.auth.dto.AuthUserDTO
import com.tul.backend.auth.dto.LoginDTO
import com.tul.backend.auth.dto.RegisterDTO
import com.tul.backend.auth.repository.AuthUserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class AuthUserService(
    private val authUserRepository: AuthUserRepository,
    private val authPasswordService: AuthPasswordService,
) {
    fun login(loginDTO: LoginDTO): AuthUserDTO? {
        if (!loginDTO.isValid()) {
            log.warn { "LoginDTO: $loginDTO is invalid" }
            return null
        }

        val authUser = authUserRepository.findByEmail(loginDTO.email.value)
        if (authUser == null) {
            log.warn { "User with email: ${loginDTO.email.value} not found" }
            return null
        }

        return authPasswordService.login(loginDTO, authUser)
    }

    fun register(registerDTO: RegisterDTO): AuthUserDTO? {
        return null
    }

    fun logout(userId: Long): Boolean {
        return false
    }
}
