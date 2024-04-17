package com.tul.backend.weather.service.parser

import com.tul.backend.objectMapper
import com.tul.backend.weather.dto.CurrentWeatherDTO
import com.tul.backend.weather.dto.ForecastWeatherDTO
import com.tul.backend.weather.dto.LocationDTO
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime

class WebClientParserTests : FeatureSpec({

  feature("parseLocationJson") {
    scenario("should return LocationDTO") {
      val spec = getSpec()
      val json = """
        [
          {
            "lat": 51.506321,
            "lon": -0.12714
          }
        ]
      """.trimIndent()
      val expectedResult = LocationDTO(51.506321, -0.12714)

      val result = spec.webClientParser.parseLocationJson(json)!!

      result.latitude shouldBe expectedResult.latitude
      result.longitude shouldBe expectedResult.longitude
    }

    scenario("should return null") {
      val spec = getSpec()
      val json = "invalid json"

      val result = spec.webClientParser.parseLocationJson(json)

      result shouldBe null
    }
  }

  feature("parseCurrentWeatherJson") {
    scenario("should return CurrentWeatherDTO") {
      val spec = getSpec()
      val json = """
        {
          "current": {
              "time": "2021-08-01T00:00:00.000Z",
              "temperature_2m": 15.0,
              "cloud_cover": 1,
              "is_day": 1,
              "wind_speed_10": 20.0
          }
        }
      """.trimIndent()
      val expectedResult = CurrentWeatherDTO(
        time = LocalDateTime.parse("2021-08-01T00:00:00"),
        temperature = 15.0,
        cloudCover = 1,
        windSpeed = 20.0,
        isDay = true
      )

      val result = spec.webClientParser.parseCurrentWeatherJson(json)!!

      result.time shouldBe expectedResult.time
      result.temperature shouldBe expectedResult.temperature
      result.cloudCover shouldBe expectedResult.cloudCover
      result.windSpeed shouldBe expectedResult.windSpeed
      result.isDay shouldBe expectedResult.isDay
    }

    scenario("should return null") {
      val spec = getSpec()
      val json = "invalid json"

      val result = spec.webClientParser.parseCurrentWeatherJson(json)

      result shouldBe null
    }
  }

  feature("parseForecastWeatherJson") {
    scenario("should return ForecastWeatherDTO") {
      val spec = getSpec()
      val json = """
        |{
        |  "daily": {
        |    "time": ["2021-08-01T00:00:00.000Z"],
        |    "temperature_2m_max": [15.0],
        |    "temperature_2m_min": [10.0],"wind_speed_10m_max": [20.0]
        |  }
        |}
      """.trimMargin()
      val expectedResult = ForecastWeatherDTO(
        time = listOf(LocalDate.parse("2021-08-01")),
        maxTemperature = listOf(15.0),
        minTemperature = listOf(10.0),
        maxWindSpeed = listOf(20.0)
      )

      val result = spec.webClientParser.parseForecastWeatherJson(json)!!

      result.time shouldBe expectedResult.time
      result.maxTemperature shouldBe expectedResult.maxTemperature
      result.minTemperature shouldBe expectedResult.minTemperature
      result.maxWindSpeed shouldBe expectedResult.maxWindSpeed
    }

    scenario("should return null") {
      val spec = getSpec()
      val json = "invalid json"

      val result = spec.webClientParser.parseForecastWeatherJson(json)

      result shouldBe null
    }
  }
})

private class WebClientParserSpecWrapper {
  val webClientParser = WebClientParser(objectMapper)
}

private fun getSpec() = WebClientParserSpecWrapper()