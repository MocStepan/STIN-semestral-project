package com.tul.backend.auth.dto

import com.tul.backend.auth.base.valueobject.EmailAddress

interface AuthUserBase {
  val email: EmailAddress
  val password: String
}