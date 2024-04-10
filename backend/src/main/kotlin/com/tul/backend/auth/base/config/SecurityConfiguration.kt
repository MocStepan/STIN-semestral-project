package com.tul.backend.auth.base.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.tul.backend.auth.base.dto.ErrorDTO
import com.tul.backend.auth.base.valueobject.UserRole
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
  private val objectMapper: ObjectMapper,
  private val authenticationProvider: AuthenticationProvider
) {
  private val userUnsecuredEndpoints =
    arrayOf(
      "/api/auth/login",
      "/api/auth/register",
    )

  private val adminUnsecuredEndpoints =
    arrayOf(
      "api/auth/test"
    )

  @Bean
  fun securityFilterChain(
    http: HttpSecurity,
    jwtAuthenticationFilter: JwtAuthenticationFilter
  ): DefaultSecurityFilterChain =
    http
      .csrf { it.disable() }
      .cors { it.disable() }
      .authorizeHttpRequests {
        it
          .requestMatchers(*userUnsecuredEndpoints).permitAll()
          .requestMatchers(*adminUnsecuredEndpoints).hasRole(UserRole.ADMIN.name)
          .anyRequest().fullyAuthenticated()
      }
      .sessionManagement { session: SessionManagementConfigurer<HttpSecurity> ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }
      .authenticationProvider(authenticationProvider)
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
      .exceptionHandling {
        it.authenticationEntryPoint(authenticationExceptionHandler)
      }
      .build()

  private val authenticationExceptionHandler =
    { _: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException ->
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      response.status = HttpServletResponse.SC_UNAUTHORIZED
      objectMapper.writeValue(response.writer, ErrorDTO("${authException.message}"))
    }
}
