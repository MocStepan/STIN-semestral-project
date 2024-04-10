package com.tul.backend.auth.base

import com.tul.backend.auth.base.config.JwtAuthenticationFilter
import com.tul.backend.auth.base.service.TokenFilterService
import com.tul.backend.auth.base.valueobject.UserRole
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.platform.commons.util.ReflectionUtils
import org.springframework.security.core.userdetails.User

class JwtAuthenticationFilterTests : FeatureSpec({

  feature("doFilterInternal") {

    scenario("login successfull") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      val filterChain = mockk<FilterChain>()
      val authUser = User.builder()
        .username("email")
        .password("password")
        .roles(UserRole.ADMIN.name)
        .build()

      every { spec.tokenFilterService.validateRequest(request) } returns authUser
      every { spec.tokenFilterService.updateContext(authUser, request, response) } just runs
      every { filterChain.doFilter(request, response) } just runs

      spec.doFilterInternal(request, response, filterChain)

      verify { filterChain.doFilter(request, response) }
    }

    scenario("login failed") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      val filterChain = mockk<FilterChain>()
      val authUser = User.builder()
        .username("email")
        .password("password")
        .roles(UserRole.ADMIN.name)
        .build()

      every { spec.tokenFilterService.validateRequest(request) } returns null
      every { filterChain.doFilter(request, response) } just runs

      spec.doFilterInternal(request, response, filterChain)

      verify { filterChain.doFilter(request, response) }
      verify(exactly = 0) { spec.tokenFilterService.updateContext(authUser, request, response) }
    }
  }
})

private val doFilterInternalMethod = ReflectionUtils.findMethod(
  JwtAuthenticationFilter::class.java,
  "doFilterInternal",
  HttpServletRequest::class.java,
  HttpServletResponse::class.java,
  FilterChain::class.java
).get()

private class AuthJwtServiceSpecWrapper(
  val tokenFilterService: TokenFilterService
) {
  val jwtAuthenticationFilter = JwtAuthenticationFilter(
    tokenFilterService,
  )

  // via reflection, because the method is protected
  fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) = ReflectionUtils.invokeMethod(
    doFilterInternalMethod,
    jwtAuthenticationFilter,
    request,
    response,
    filterChain
  )
}

private fun getSpec() = AuthJwtServiceSpecWrapper(mockk())