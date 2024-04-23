package com.tul.backend.auth.base.config

import io.kotest.core.spec.style.FeatureSpec

class JwtConfigurationTests : FeatureSpec({

  feature("JwtConfiguration") {

    scenario("logoutSuccessHandler bean") {
      JwtConfiguration().logoutSuccessHandler()
    }

    scenario("cookieClearingLogoutHandler bean") {
      JwtConfiguration().cookieClearingLogoutHandler()
    }
  }
})