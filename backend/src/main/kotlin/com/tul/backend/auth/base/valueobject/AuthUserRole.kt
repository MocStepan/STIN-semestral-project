package com.tul.backend.auth.base.valueobject

import org.springframework.security.core.GrantedAuthority

enum class AuthUserRole : GrantedAuthority {
  USER,
  ADMIN;

  override fun getAuthority(): String {
    return name
  }
}
