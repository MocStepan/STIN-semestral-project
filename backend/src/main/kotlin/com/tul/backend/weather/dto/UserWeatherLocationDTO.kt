package com.tul.backend.weather.dto

import com.tul.backend.weather.entity.UserWeatherLocation

data class UserWeatherLocationDTO(
  val id: Long,
  val location: String
) {
  companion object {
    fun from(userWeatherLocation: UserWeatherLocation): UserWeatherLocationDTO {
      return UserWeatherLocationDTO(
        id = userWeatherLocation.id,
        location = userWeatherLocation.location
      )
    }
  }
}
