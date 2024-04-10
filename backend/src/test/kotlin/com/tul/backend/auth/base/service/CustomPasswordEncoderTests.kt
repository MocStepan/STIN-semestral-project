package com.tul.backend.auth.base.service

import io.kotest.core.spec.style.FeatureSpec

class CustomPasswordEncoderTests : FeatureSpec({

  val customPasswordEncoder = CustomPasswordEncoder()

  feature("encode") {
    scenario("encode password") {
      val password = "password"
      val encodedPassword = customPasswordEncoder.encode(password)
      val matches = customPasswordEncoder.matches(password, encodedPassword)
      assert(matches)
    }
  }

  feature("matches") {
    scenario("password matches") {
      val password = "password"
      val encodedPassword = customPasswordEncoder.encode(password)
      val matches = customPasswordEncoder.matches(password, encodedPassword)
      assert(matches)
    }

    scenario("password does not match") {
      val password = "password"
      val encodedPassword = customPasswordEncoder.encode(password)
      val matches = customPasswordEncoder.matches("wrongPassword", encodedPassword)
      assert(!matches)
    }
  }
})