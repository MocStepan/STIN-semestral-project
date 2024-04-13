package com.tul.backend.auth.base.config

import com.tul.backend.auth.repository.AuthUserRepository
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration

class JwtConfigurationTests : FeatureSpec({

  feature("JwtConfiguration") {

    scenario("userDetailService bean") {
      val authUserRepository = mockk<AuthUserRepository>()
      val jwtConfiguration = JwtConfiguration()

      jwtConfiguration.userDetailService(authUserRepository)
    }

    scenario("encoder bean") {
      JwtConfiguration().encoder()
    }

    scenario("authenticationProvider bean") {
      val authUserRepository = mockk<AuthUserRepository>()
      val jwtConfiguration = JwtConfiguration()
      jwtConfiguration.authenticationProvider(authUserRepository)
    }

    scenario("authenticationManager bean") {
      val config = mockk<AuthenticationConfiguration>()
      val jwtConfiguration = JwtConfiguration()
      every { config.authenticationManager } returns mockk()
      jwtConfiguration.authenticationManager(config)
    }

    scenario("logoutSuccessHandler bean") {
      JwtConfiguration().logoutSuccessHandler()
    }

    scenario("cookieClearingLogoutHandler bean") {
      JwtConfiguration().cookieClearingLogoutHandler()
    }
  }
})