package com.tul.backend.weather.service

import com.tul.backend.weather.dto.CurrentWeatherDTO
import com.tul.backend.weather.dto.ForecastWeatherDTO
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class WeatherService(
  private val weatherHandler: WeatherHandler
) {
  fun getCurrentWeather(location: String): CurrentWeatherDTO? {
    val currentWeatherDTO = weatherHandler.getCurrentWeather(location)
    if (currentWeatherDTO == null) {
      log.warn { "No current weather found for $location" }
      return null
    }
    return currentWeatherDTO
  }

  fun getForecastWeather(location: String): ForecastWeatherDTO? {
    val forecastWeatherDTO = weatherHandler.getForecastWeather(location)
    if (forecastWeatherDTO == null) {
      log.warn { "No forecast weather found for $location" }
      return null
    }
    return forecastWeatherDTO
  }
}
