package com.tul.backend.weather.dto

import com.tul.backend.weather.valueobject.ForecastWeatherJson
import java.time.LocalDate

data class ForecastWeatherDTO(
  val time: List<LocalDate>,
  val maxTemperature: List<Double>,
  val minTemperature: List<Double>,
  val maxWindSpeed: List<Double>
) {

  companion object {
    fun from(forecastWeatherJson: ForecastWeatherJson): ForecastWeatherDTO {
      return ForecastWeatherDTO(
        forecastWeatherJson.daily.time,
        forecastWeatherJson.daily.maxTemperature,
        forecastWeatherJson.daily.minTemperature,
        forecastWeatherJson.daily.maxWindSpeed
      )
    }
  }
}
