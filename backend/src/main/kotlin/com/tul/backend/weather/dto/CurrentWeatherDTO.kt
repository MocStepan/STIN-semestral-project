package com.tul.backend.weather.dto

import com.tul.backend.weather.valueobject.CurrentWeatherJson
import java.time.LocalDateTime

data class CurrentWeatherDTO(
  val time: LocalDateTime,
  val temperature: Double,
  val cloudCover: Int,
  val windSpeed: Double,
  val isDay: Boolean
) {

  companion object {
    fun from(currentWeatherJson: CurrentWeatherJson): CurrentWeatherDTO {
      return CurrentWeatherDTO(
        currentWeatherJson.current.time,
        currentWeatherJson.current.temperature,
        currentWeatherJson.current.cloudCover,
        currentWeatherJson.current.windSpeed,
        currentWeatherJson.current.isDay == 1
      )
    }
  }
}