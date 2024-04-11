package com.tul.backend.auth.base.config

import com.tul.backend.auth.base.service.CustomPasswordEncoder
import com.tul.backend.auth.base.service.CustomUserDetailsService
import com.tul.backend.auth.repository.AuthUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
class JwtConfiguration {

  @Bean
  fun userDetailService(authUserRepository: AuthUserRepository): UserDetailsService =
      CustomUserDetailsService(authUserRepository)

  @Bean
  fun encoder(): PasswordEncoder = CustomPasswordEncoder()

  @Bean
  fun authenticationProvider(authUserRepository: AuthUserRepository): AuthenticationProvider =
      DaoAuthenticationProvider()
          .also {
            it.setUserDetailsService(userDetailService(authUserRepository))
            it.setPasswordEncoder(encoder())
          }

  @Bean
  fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
      config.authenticationManager
}
