package com.tul.stin.weather.controller

import com.tul.stin.weather.service.WeatherService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class WeatherController(
    private val weatherService: WeatherService
) {


    @GetMapping("/actual-weather")
    fun getActualWeather() {
        val response = weatherService.getActualWeather()
        val status = if (response != null) HttpStatus.OK else HttpStatus.NOT_FOUND
        // return ResponseEntity(response, status)
    }
}