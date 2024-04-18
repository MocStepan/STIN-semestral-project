package com.tul.backend.weather.service

import com.tul.backend.weather.dto.CurrentWeatherDTO
import com.tul.backend.weather.dto.ForecastWeatherDTO
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalDateTime

class WeatherServiceTests : FeatureSpec({

  feature("getCurrentWeather") {
    scenario("should return current weather") {
      val spec = getSpec()
      val location = "location"
      val currentWeatherDTO = CurrentWeatherDTO(
        time = LocalDateTime.now(),
        temperature = 20.0,
        cloudCover = 50,
        windSpeed = 10.0,
        isDay = true
      )

      every { spec.weatherHandler.getCurrentWeather(location) } returns currentWeatherDTO

      val result = spec.weatherService.getCurrentWeather(location)

      result shouldBe currentWeatherDTO
    }

    scenario("should return nul, when current weather is not found") {
      val spec = getSpec()
      val location = "location"

      every { spec.weatherHandler.getCurrentWeather(location) } returns null

      val result = spec.weatherService.getCurrentWeather(location)

      result shouldBe null
    }
  }

  feature("getForecastWeather") {

    scenario("should return forecast weather") {
      val spec = getSpec()
      val location = "location"
      val forecastWeatherDTO = ForecastWeatherDTO(
        time = listOf(LocalDate.now()),
        maxTemperature = listOf(20.0),
        minTemperature = listOf(10.0),
        maxWindSpeed = listOf(10.0),
      )

      every { spec.weatherHandler.getForecastWeather(location) } returns forecastWeatherDTO

      val result = spec.weatherService.getForecastWeather(location)

      result shouldBe forecastWeatherDTO
    }

    scenario("should return nul, when forecast weather is not found") {
      val spec = getSpec()
      val location = "location"

      every { spec.weatherHandler.getForecastWeather(location) } returns null

      val result = spec.weatherService.getForecastWeather(location)

      result shouldBe null
    }
  }
})

private class WeatherServiceSpecWrapper(
  var weatherHandler: WeatherHandler
) {
  val weatherService = WeatherService(weatherHandler)
}

private fun getSpec() = WeatherServiceSpecWrapper(mockk())