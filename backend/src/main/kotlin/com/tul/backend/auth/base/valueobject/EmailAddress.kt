package com.tul.backend.auth.base.valueobject

private val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")

@JvmInline
value class EmailAddress(val value: String) {
  override fun toString(): String {
    return value
  }

  fun isValid(): Boolean {
    return value.matches(regex)
  }
}
