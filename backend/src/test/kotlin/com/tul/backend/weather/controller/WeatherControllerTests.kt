package com.tul.backend.weather.controller

import com.tul.backend.weather.dto.CurrentWeatherDTO
import com.tul.backend.weather.dto.ForecastWeatherDTO
import com.tul.backend.weather.service.WeatherService
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import java.time.LocalDate
import java.time.LocalDateTime

class WeatherControllerTests : FeatureSpec({

  feature("getCurrentWeather") {
    scenario("should return current weather") {
      val spec = getSpec()
      val currentWeather = CurrentWeatherDTO(
        time = LocalDateTime.now(),
        temperature = 10.0,
        cloudCover = 90,
        windSpeed = 10.0,
        isDay = true,
      )

      every { spec.weatherService.getCurrentWeather("test") } returns currentWeather

      val response = spec.weatherController.getCurrentWeather("test")

      response.body shouldBe currentWeather
      response.statusCode shouldBe HttpStatus.OK
      val body = response.body!!
      body.time shouldBe currentWeather.time
      body.temperature shouldBe currentWeather.temperature
      body.cloudCover shouldBe currentWeather.cloudCover
      body.windSpeed shouldBe currentWeather.windSpeed
      body.isDay shouldBe currentWeather.isDay
    }

    scenario("should return null with status not fount") {
      val spec = getSpec()

      every { spec.weatherService.getCurrentWeather("test") } returns null

      val response = spec.weatherController.getCurrentWeather("test")

      response.body shouldBe null
      response.statusCode shouldBe HttpStatus.NOT_FOUND
    }
  }

  feature("getForecastWeather") {
    scenario("should return forecast weather") {
      val spec = getSpec()
      val forecastWeather = ForecastWeatherDTO(
        time = listOf(LocalDate.now()),
        maxTemperature = listOf(10.0),
        minTemperature = listOf(5.0),
        maxWindSpeed = listOf(10.0)
      )

      every { spec.weatherService.getForecastWeather("test") } returns forecastWeather

      val response = spec.weatherController.getForecastWeather("test")

      response.body shouldBe forecastWeather
      response.statusCode shouldBe HttpStatus.OK
      val body = response.body!!
      body.time shouldBe forecastWeather.time
      body.maxTemperature shouldBe forecastWeather.maxTemperature
      body.minTemperature shouldBe forecastWeather.minTemperature
      body.maxWindSpeed shouldBe forecastWeather.maxWindSpeed
    }

    scenario("should return null with status not fount") {
      val spec = getSpec()

      every { spec.weatherService.getForecastWeather("test") } returns null

      val response = spec.weatherController.getForecastWeather("test")

      response.body shouldBe null
      response.statusCode shouldBe HttpStatus.NOT_FOUND
    }
  }
})

private class WeatherControllerSpecWrapper(
  val weatherService: WeatherService
) {
  val weatherController = WeatherController(weatherService)
}

private fun getSpec() = WeatherControllerSpecWrapper(mockk())