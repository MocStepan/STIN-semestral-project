package com.tul.backend.weather.entity

import com.tul.backend.auth.entity.AuthUser
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class UserWeatherLocation(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long = 0L,
  val location: String,
  @ManyToOne(optional = false)
  val user: AuthUser
) {

  companion object {
    fun from(location: String, user: AuthUser): UserWeatherLocation {
      return UserWeatherLocation(
        location = location,
        user = user
      )
    }
  }

}
