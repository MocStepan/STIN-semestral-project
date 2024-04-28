package com.tul.backend.weather.controller

import com.tul.backend.auth.base.dto.AuthJwtClaims
import com.tul.backend.weather.dto.CurrentWeatherDTO
import com.tul.backend.weather.dto.ForecastWeatherDTO
import com.tul.backend.weather.dto.UserWeatherLocationDTO
import com.tul.backend.weather.service.WeatherService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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

  @PostMapping("/v1/weather/location")
  fun saveUserWeatherLocation(
    @RequestBody location: String,
    authentication: Authentication
  ): ResponseEntity<Boolean> {
    val claims = authentication.principal as AuthJwtClaims
    val response = weatherService.saveUserWeatherLocation(location, claims)
    val status = if (response) HttpStatus.OK else HttpStatus.BAD_REQUEST
    return ResponseEntity(response, status)
  }

  @GetMapping("/v1/weather/locations")
  fun getUserWeatherLocations(
    authentication: Authentication
  ): ResponseEntity<List<UserWeatherLocationDTO>?> {
    val claims = authentication.principal as AuthJwtClaims
    val response = weatherService.getUserWeatherLocations(claims)
    val status = if (response != null) HttpStatus.OK else HttpStatus.NOT_FOUND
    return ResponseEntity(response, status)
  }

  @DeleteMapping("/v1/weather/location/{id}")
  fun deleteUserWeatherLocation(
    @PathVariable id: Long,
    authentication: Authentication
  ): ResponseEntity<Boolean> {
    val claims = authentication.principal as AuthJwtClaims
    val response = weatherService.deleteUserWeatherLocation(id, claims)
    val status = if (response) HttpStatus.OK else HttpStatus.NOT_FOUND
    return ResponseEntity(response, status)
  }
}
