package com.tul.backend

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tul.backend.auth.base.dto.AccessTokenClaims
import com.tul.backend.auth.base.dto.AuthJwtClaims
import com.tul.backend.auth.base.valueobject.AuthUserRole
import com.tul.backend.auth.base.valueobject.EmailAddress
import com.tul.backend.auth.entity.AuthUser
import com.tul.backend.shared.jackson.TrimmingStringDeserializer

val objectMapper: ObjectMapper = jacksonObjectMapper()
  .registerModule(JavaTimeModule())
  .registerModule(SimpleModule().addDeserializer(String::class.java, TrimmingStringDeserializer()))


fun createAuthUser(
  id: Long = 0L,
  username: String = "test",
  email: EmailAddress = EmailAddress("test@test.cz"),
  password: String = "test",
  role: AuthUserRole = AuthUserRole.USER
): AuthUser {
  return AuthUser(
    id = id,
    username = username,
    email = email,
    password = password,
    role = role
  )
}

fun createAccessTokenClaims(
  authUserId: Long = 0L,
  role: AuthUserRole = AuthUserRole.USER,
  email: String = "test@test.cz"
) = AccessTokenClaims(
  authUserId = authUserId,
  authUserRole = role,
  email = EmailAddress(email)
)

fun createUserClaims(): AuthJwtClaims = createAccessTokenClaims()
