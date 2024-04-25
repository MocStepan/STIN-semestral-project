package com.tul.backend.weather.service

import com.tul.backend.auth.base.dto.AuthJwtClaims
import com.tul.backend.auth.repository.AuthUserRepository
import com.tul.backend.weather.dto.CurrentWeatherDTO
import com.tul.backend.weather.dto.ForecastWeatherDTO
import com.tul.backend.weather.entity.UserWeatherLocation
import com.tul.backend.weather.repository.UserWeatherLocationRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class WeatherService(
  private val weatherHandler: WeatherHandler,
  private val authUserRepository: AuthUserRepository,
  private val userWeatherLocationRepository: UserWeatherLocationRepository
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

  fun saveFavoriteLocation(location: String, claims: AuthJwtClaims): Boolean {
    val authUser = authUserRepository.findByIdOrNull(claims.authUserId)
    if (authUser == null) {
      log.warn { "No user found for ${claims.authUserId}" }
      return false
    }

    userWeatherLocationRepository.save(UserWeatherLocation(location = location, user = authUser))
    return true
  }
}
