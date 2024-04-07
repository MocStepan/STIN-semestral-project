package com.tul.backend.auth.base.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.tul.backend.auth.base.dto.ErrorDTO
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.servlet.handler.HandlerMappingIntrospector

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val objectMapper: ObjectMapper,
) {
    private val unsecuredEndpoints =
        arrayOf(
            "/api/login",
            "/api/register",
            "/api/actual-weather",
        )

    @Bean
    fun mvc(introspector: HandlerMappingIntrospector): MvcRequestMatcher.Builder {
        return MvcRequestMatcher.Builder(introspector)
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        mvc: MvcRequestMatcher.Builder,
    ): SecurityFilterChain? {
        val unsecuredEndpointsMatchers = unsecuredEndpoints.map { mvc.pattern(it) }.toTypedArray()

        http.authorizeHttpRequests {
            it.requestMatchers(*unsecuredEndpointsMatchers).permitAll()
                .anyRequest().authenticated()
        }.exceptionHandling {
            it.authenticationEntryPoint(authenticationExceptionHandler)
        }.formLogin {
        }.csrf {
        }.cors {
            it.configurationSource(corsConfigurationSource())
        }.logout {
        }.httpBasic {
        }
        return http.build()
    }

    private val authenticationExceptionHandler =
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
