package com.tul.backend.weather.controller

import com.tul.backend.weather.service.WeatherService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class WeatherController(
  private val weatherService: WeatherService,
) {
  @GetMapping("/auth/weather/actual-weather")
  fun getActualWeather(): ResponseEntity<String?> {
    val response = weatherService.getActualWeather()
    val status = if (response != null) HttpStatus.OK else HttpStatus.NOT_FOUND
    return ResponseEntity(response, status)
  }
}
