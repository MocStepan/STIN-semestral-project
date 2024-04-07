package com.tul.backend.auth.entity

import com.tul.backend.auth.base.valueobject.EmailAddress
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class AuthUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    var username: String,
    var email: EmailAddress,
    var password: String,
)
