package com.tul.backend.weather.service.parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.tul.backend.weather.dto.CurrentWeatherDTO
import com.tul.backend.weather.dto.ForecastWeatherDTO
import com.tul.backend.weather.dto.LocationDTO
import com.tul.backend.weather.valueobject.CurrentWeatherJson
import com.tul.backend.weather.valueobject.ForecastWeatherJson
import com.tul.backend.weather.valueobject.LocationOutcomeJsonList
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {}

@Component
class WebClientParser(
  private val objectMapper: ObjectMapper
) {
  fun parseLocationJson(json: String): LocationDTO? {
    return try {
      val response = objectMapper.readValue(json, LocationOutcomeJsonList::class.java).results
      LocationDTO.from(response.first())
    } catch (e: Exception) {
      log.error(e) { "Error parsing location json" }
      null
    }
  }

  fun parseCurrentWeatherJson(json: String): CurrentWeatherDTO? {
    return try {
      val response = objectMapper.readValue(json, CurrentWeatherJson::class.java)
      CurrentWeatherDTO.from(response)
    } catch (e: Exception) {
      log.error(e) { "Error parsing current weather json" }
      null
    }
  }

  fun parseForecastWeatherJson(json: String): ForecastWeatherDTO? {
    return try {
      val response = objectMapper.readValue(json, ForecastWeatherJson::class.java)
      ForecastWeatherDTO.from(response)
    } catch (e: Exception) {
      log.error(e) { "Error parsing forecast weather json" }
      null
    }
  }
}