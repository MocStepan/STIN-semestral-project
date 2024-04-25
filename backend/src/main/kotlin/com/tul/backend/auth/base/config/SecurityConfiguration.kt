package com.tul.backend.auth.base.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.tul.backend.auth.base.dto.ErrorDTO
import com.tul.backend.auth.base.valueobject.AuthUserRole
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
  private val objectMapper: ObjectMapper,
  private val logoutSuccessHandler: LogoutSuccessHandler,
  private val cookieClearingLogoutHandler: LogoutHandler,
  private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

  private val userUnsecuredEndpoints =
    arrayOf(
      "/api/v1/auth/login",
      "/api/v1/auth/register",
      "/api/v1/weather/current/*",
    )

  private val adminUnsecuredEndpoints =
    arrayOf(
      "api/auth/test",
    )

  @Bean
  fun securityFilterChain(http: HttpSecurity): DefaultSecurityFilterChain =
    http
      .csrf { it.disable() }
      .cors {
        it.configurationSource(corsConfigurationSource())
      }
      .authorizeHttpRequests {
        it
          .requestMatchers(*userUnsecuredEndpoints).permitAll()
          .requestMatchers(*adminUnsecuredEndpoints).hasAuthority(AuthUserRole.ADMIN.name)
          .anyRequest().authenticated()
      }
      .sessionManagement { session: SessionManagementConfigurer<HttpSecurity> ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
      .exceptionHandling {
        it.authenticationEntryPoint(authenticationExceptionHandler)
      }
      .logout {
        it.logoutUrl("/api/auth/logout")
          .addLogoutHandler(cookieClearingLogoutHandler)
          .logoutSuccessHandler(logoutSuccessHandler)
          .deleteCookies("access_token")
          .permitAll()
      }
      .build()

  val authenticationExceptionHandler =
    { _: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException ->
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      response.status = HttpServletResponse.SC_UNAUTHORIZED
      objectMapper.writeValue(response.writer, ErrorDTO("${authException.message}"))
    }

  private fun corsConfigurationSource(): CorsConfigurationSource {
    return CorsConfigurationSource {
      CorsConfiguration().apply {
        allowedMethods = listOf("*")
        allowedHeaders = listOf("*")
        exposedHeaders = listOf("Content-Disposition")
        allowedOriginPatterns = listOf("*")
        allowCredentials = true
      }
    }
  }
}
