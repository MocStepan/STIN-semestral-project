package com.tul.backend.auth.base.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.tul.backend.auth.base.dto.ErrorDTO
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.AuthenticationException
import java.io.PrintWriter

@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfigurationTests : FeatureSpec({

  feature("SecurityConfiguration") {

    scenario("securityFilterChain configuration") {
      val objectMapper = ObjectMapper()
      val jwtConfiguration = JwtConfiguration()
      val authenticationProvider = jwtConfiguration.authenticationProvider(mockk())
      val logoutSuccessHandler = jwtConfiguration.logoutSuccessHandler()
      val cookieClearingLogoutHandler = jwtConfiguration.cookieClearingLogoutHandler()

      val securityConfiguration = SecurityConfiguration(
        objectMapper,
        authenticationProvider,
        logoutSuccessHandler,
        cookieClearingLogoutHandler
      )

      val httpSecurity = mockk<HttpSecurity>()
      val jwtAuthenticationFilter = mockk<JwtAuthenticationFilter>()

      every { httpSecurity.csrf(any()) } returns httpSecurity
      every { httpSecurity.cors(any()) } returns httpSecurity
      every { httpSecurity.authorizeHttpRequests(any()) } returns httpSecurity
      every { httpSecurity.sessionManagement(any()) } returns httpSecurity
      every { httpSecurity.authenticationProvider(any()) } returns httpSecurity
      every { httpSecurity.addFilterBefore(any(), any()) } returns httpSecurity
      every { httpSecurity.logout(any()) } returns httpSecurity
      every { httpSecurity.exceptionHandling(any()) } returns httpSecurity
      every { httpSecurity.build() } returns mockk()

      securityConfiguration.securityFilterChain(httpSecurity, jwtAuthenticationFilter)
    }

    scenario("authenticationExceptionHandler function") {
      val objectMapper = ObjectMapper()
      val jwtConfiguration = JwtConfiguration()
      val authenticationProvider = jwtConfiguration.authenticationProvider(mockk())
      val logoutSuccessHandler = jwtConfiguration.logoutSuccessHandler()
      val cookieClearingLogoutHandler = jwtConfiguration.cookieClearingLogoutHandler()

      val securityConfiguration = SecurityConfiguration(
        objectMapper,
        authenticationProvider,
        logoutSuccessHandler,
        cookieClearingLogoutHandler
      )
      val errorDTO = ErrorDTO("Unauthorized")

      val authenticationExceptionHandler = securityConfiguration.authenticationExceptionHandler

      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      val authException = mockk<AuthenticationException>()
      val printWriter = PrintWriter(System.out)

      every { response.contentType = MediaType.APPLICATION_JSON_VALUE } returns Unit
      every { response.status = HttpServletResponse.SC_UNAUTHORIZED } returns Unit
      every { response.writer } returns printWriter
      every { authException.message } returns errorDTO.message
      every { response.contentType } returns MediaType.APPLICATION_JSON_VALUE
      every { response.status } returns HttpServletResponse.SC_UNAUTHORIZED

      authenticationExceptionHandler(request, response, authException)

      response.contentType shouldBe MediaType.APPLICATION_JSON_VALUE
      response.status shouldBe HttpServletResponse.SC_UNAUTHORIZED
    }
  }
})