package com.tul.backend.auth.base.dto

import com.tul.backend.auth.base.valueobject.AuthUserRole
import com.tul.backend.auth.base.valueobject.EmailAddress
import org.springframework.security.core.AuthenticatedPrincipal

interface AuthJwtClaims : AuthenticatedPrincipal {
  val authUserId: Long
  val authUserRole: AuthUserRole
  val email: EmailAddress
}
