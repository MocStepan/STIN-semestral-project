package com.tul.backend.auth.base.dto

import com.tul.backend.auth.base.valueobject.AuthUserRole
import com.tul.backend.auth.base.valueobject.EmailAddress
import com.tul.backend.auth.entity.AuthUser
import org.springframework.security.core.AuthenticatedPrincipal

data class AccessTokenClaims(
  val authUserId: Long,
  val authUserRole: AuthUserRole,
  val email: EmailAddress
) : AuthenticatedPrincipal {

  constructor(authUser: AuthUser) : this(
    authUserId = authUser.id,
    authUserRole = authUser.role,
    email = authUser.email
  )

  override fun getName(): String = email.value
}