package com.tul.backend.auth.service

import com.tul.backend.auth.base.service.AccessTokenService
import com.tul.backend.auth.base.service.CustomPasswordEncoder
import com.tul.backend.auth.dto.SignInDTO
import com.tul.backend.auth.dto.SignUpDTO
import com.tul.backend.auth.entity.AuthUser
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthenticationHandler(
  private val accessTokenService: AccessTokenService,
  private val customPasswordEncoder: CustomPasswordEncoder
) {
  fun authenticate(
    signInDTO: SignInDTO,
    authUser: AuthUser,
    response: HttpServletResponse
  ): Boolean {
    return if (customPasswordEncoder.matches(signInDTO.password, authUser.password)) {
      val claims = accessTokenService.createClaims(authUser)
      val cookie = accessTokenService.createCookie(claims)
      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
      true
    } else {
      false
    }
  }

  fun hashSignUpPassword(signUpDTO: SignUpDTO): AuthUser {
    val authUser = AuthUser.from(signUpDTO)
    authUser.password = customPasswordEncoder.encode(authUser.password)
    return authUser
  }
}
