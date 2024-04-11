package com.tul.backend.auth.base.service

import com.tul.backend.auth.repository.AuthUserRepository
import com.tul.backend.createAuthUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.core.userdetails.User

class CustomUserDetailsServiceTests : FeatureSpec({

  feature("loadUserByUsername") {

    scenario("user found") {
      val spec = getSpec()
      val authUser = createAuthUser()
      val userDetails = User.builder()
          .username(authUser.email.value)
          .password(authUser.password)
          .roles(authUser.role.name)
          .build()

      every { spec.authUserRepository.findByEmail(authUser.email.value) } returns authUser

      val result = spec.customUserDetailsService.loadUserByUsername(authUser.email.value)

      verify { spec.authUserRepository.findByEmail(authUser.email.value) }
      result shouldBe userDetails
    }

    scenario("user not found") {
      val spec = getSpec()
      val email = "email"

      every { spec.authUserRepository.findByEmail(email) } returns null

      shouldThrow<Exception> {
        spec.customUserDetailsService.loadUserByUsername(email)
      }
      verify { spec.authUserRepository.findByEmail(email) }
    }

    scenario("email is null") {
      val spec = getSpec()

      shouldThrow<Exception> {
        spec.customUserDetailsService.loadUserByUsername(null)
      }
    }
  }
})

private class CustomUserDetailsServiceSpecWrapper(
    val authUserRepository: AuthUserRepository
) {
  val customUserDetailsService = CustomUserDetailsService(
      authUserRepository
  )
}

private fun getSpec() = CustomUserDetailsServiceSpecWrapper(mockk())
