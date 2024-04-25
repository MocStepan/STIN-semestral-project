package com.tul.backend.weather.controller

import com.tul.backend.auth.base.dto.AuthJwtClaims
import com.tul.backend.auth.base.dto.JwtClaims
import com.tul.backend.weather.dto.CurrentWeatherDTO
import com.tul.backend.weather.dto.ForecastWeatherDTO
import com.tul.backend.weather.service.WeatherService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class WeatherController(
  private val weatherService: WeatherService,
) {
  @GetMapping("/v1/weather/current/{location}")
  fun getCurrentWeather(
    @PathVariable location: String
  ): ResponseEntity<CurrentWeatherDTO?> {
    val response = weatherService.getCurrentWeather(location)
    val status = if (response != null) HttpStatus.OK else HttpStatus.NOT_FOUND
    return ResponseEntity(response, status)
  }

  @GetMapping("/v1/weather/forecast/{location}")
  fun getForecastWeather(
    @PathVariable location: String
  ): ResponseEntity<ForecastWeatherDTO?> {
    val response = weatherService.getForecastWeather(location)
    val status = if (response != null) HttpStatus.OK else HttpStatus.NOT_FOUND
    return ResponseEntity(response, status)
  }

  @PostMapping("/v1/weather/favorite/{location}")
  fun saveFavoriteLocation(
    @PathVariable location: String,
    authentication: Authentication
  ): ResponseEntity<Boolean> {
    val claims = authentication.principal as AuthJwtClaims
    val response = weatherService.saveFavoriteLocation(location, claims)
    val status = if (response) HttpStatus.OK else HttpStatus.NOT_FOUND
    return ResponseEntity(response, status)
  }
}
