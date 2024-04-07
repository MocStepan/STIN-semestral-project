package com.tul.backend.auth.base.valueobject

private val regex = Regex("^[\\w-.]+@([\\w-]+\\.)")

@JvmInline
value class EmailAddress(val value: String) {
    override fun toString(): String {
        return value
    }

    fun isValid(): Boolean {
        return value.matches(regex)
    }
}
