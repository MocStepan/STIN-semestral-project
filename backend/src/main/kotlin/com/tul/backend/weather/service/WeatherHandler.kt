package com.tul.backend.weather.service

import com.tul.backend.weather.dto.CurrentWeatherDTO
import com.tul.backend.weather.dto.ForecastWeatherDTO
import com.tul.backend.weather.service.extractor.WeatherExtractor
import com.tul.backend.weather.service.parser.WebClientParser
import com.tul.backend.weather.valueobject.WeatherStatus.CURRENT
import com.tul.backend.weather.valueobject.WeatherStatus.FORECAST
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class WeatherHandler(
  weatherExtractors: List<WeatherExtractor>,
  private val webClientParser: WebClientParser
) {

  private val weatherExtractorList = weatherExtractors.associateBy { it.weatherStatus }

  fun getCurrentWeather(location: String): CurrentWeatherDTO? {
    val weatherExtractor = weatherExtractorList[CURRENT]
    if (weatherExtractor == null) {
      log.warn { "No weather extractor found for $CURRENT" }
      return null
    }
    val weatherJson = weatherExtractor.getWeatherJson(location) ?: return null
    return webClientParser.parseCurrentWeatherJson(weatherJson)
  }

  fun getForecastWeather(location: String): ForecastWeatherDTO? {
    val weatherExtractor = weatherExtractorList[FORECAST]
    if (weatherExtractor == null) {
      log.warn { "No weather extractor found for $FORECAST" }
      return null
    }
    val weatherJson = weatherExtractor.getWeatherJson(location) ?: return null
    return webClientParser.parseForecastWeatherJson(weatherJson)
  }
}