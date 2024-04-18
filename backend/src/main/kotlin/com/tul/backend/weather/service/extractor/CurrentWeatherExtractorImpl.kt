package com.tul.backend.weather.service.extractor

import com.tul.backend.weather.dto.LocationDTO
import com.tul.backend.weather.service.client.OpenMeteoClientService
import com.tul.backend.weather.valueobject.WeatherStatus
import com.tul.backend.weather.valueobject.WeatherStatus.CURRENT
import com.tul.backend.weather.valueobject.WebClientOutcome
import com.tul.backend.weather.valueobject.WebClientOutcome.Failure
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class CurrentWeatherExtractorImpl(
  private val locationExtractor: LocationExtractor,
  private val openMeteoClientService: OpenMeteoClientService
) : WeatherExtractor {

  override val weatherStatus: WeatherStatus = CURRENT

  override fun getWeatherJson(location: String): String? {
    return runBlocking {
      val locationDTO = locationExtractor.getLocation(location) ?: return@runBlocking null
      return@runBlocking when (val currentWeather = getCurrentWeather(locationDTO)) {
        null -> null
        else -> currentWeather
      }
    }
  }

  suspend fun getCurrentWeather(locationDTO: LocationDTO): String? {
    return when (val locationJson = openMeteoClientService.getCurrentWeather(locationDTO)) {
      is Failure -> {
        processWebClientResponse(locationJson)
        null
      }

      is WebClientOutcome.Success -> {
        return locationJson.response
      }
    }
  }

  suspend fun processWebClientResponse(failure: Failure) {
    when (failure) {
      is Failure.ServerError -> {
        log.warn { "OpenMeteo clint threw serverError, while processing current weather" }
      }

      is Failure.UnspecifiedError -> {
        log.error(failure.exception) { "OpenMeteo client threw unspecified error, while processing current weather" }
      }
    }
  }
}