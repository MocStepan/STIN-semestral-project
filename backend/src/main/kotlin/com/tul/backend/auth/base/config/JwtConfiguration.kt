package com.tul.backend.auth.base.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler


@Configuration
class JwtConfiguration {

  @Bean
  fun logoutSuccessHandler(): LogoutSuccessHandler {
    val handler = SimpleUrlLogoutSuccessHandler()
    handler.setUseReferer(true)
    return handler
  }

  @Bean
  fun cookieClearingLogoutHandler(): LogoutHandler {
    return CookieClearingLogoutHandler("access_token")
  }
}
