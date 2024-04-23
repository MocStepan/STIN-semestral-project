package com.tul.backend.auth.base.config

import com.tul.backend.auth.base.dto.AccessTokenClaims
import com.tul.backend.auth.base.service.TokenFilter
import com.tul.backend.createAuthUser
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

class JwtAuthenticationFilterTests : FeatureSpec({

  feature("doFilterInternal") {

    scenario("login successfull") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      val filterChain = mockk<FilterChain>()
      val authUser = createAuthUser()
      val claims = AccessTokenClaims(authUser)

      every { spec.tokenFilter.validateRequest(request) } returns claims
      every { filterChain.doFilter(request, response) } just runs

      spec.doFilterInternal(request, response, filterChain)

      verify { filterChain.doFilter(request, response) }
    }

    scenario("login failed") {
      val spec = getSpec()
      val request = mockk<HttpServletRequest>()
      val response = mockk<HttpServletResponse>()
      val filterChain = mockk<FilterChain>()

      every { spec.tokenFilter.validateRequest(request) } returns null
      every { filterChain.doFilter(request, response) } just runs

      spec.doFilterInternal(request, response, filterChain)

      verify { filterChain.doFilter(request, response) }
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
  val tokenFilter: TokenFilter
) {
  val jwtAuthenticationFilter = JwtAuthenticationFilter(
    tokenFilter,
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
