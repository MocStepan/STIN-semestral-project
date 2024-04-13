package com.tul.backend

import com.tul.backend.auth.base.valueobject.AuthUserRole
import com.tul.backend.auth.base.valueobject.EmailAddress
import com.tul.backend.auth.entity.AuthUser

fun createAuthUser(
  id: Long = 0L,
  username: String = "admin",
  email: EmailAddress = EmailAddress("admin@admin.cz"),
  password: String = "admin",
  role: AuthUserRole = AuthUserRole.ADMIN
): AuthUser {
  return AuthUser(
    id = id,
    username = username,
    email = email,
    password = password,
    role = role
  )
}
