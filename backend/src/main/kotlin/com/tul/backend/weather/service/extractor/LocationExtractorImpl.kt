package com.tul.backend.weather.service.extractor

import com.tul.backend.weather.dto.LocationDTO
import com.tul.backend.weather.service.client.GeoDecoderClientService
import com.tul.backend.weather.service.parser.WebClientParser
import com.tul.backend.weather.valueobject.WebClientOutcome
import com.tul.backend.weather.valueobject.WebClientOutcome.Failure
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
@Transactional
class LocationExtractorImpl(
  private val geoDecoderClientService: GeoDecoderClientService,
  private val webClientParser: WebClientParser
) : LocationExtractor {

  override suspend fun getLocation(location: String): LocationDTO? {
    return when (val locationJson = geoDecoderClientService.getLocation(location)) {
      is Failure -> {
        processWebClientResponse(locationJson)
        null
      }

      is WebClientOutcome.Success -> {
        return webClientParser.parseLocationJson(locationJson.response)
      }
    }
  }


  private suspend fun processWebClientResponse(failure: Failure) {
    when (failure) {
      is Failure.ServerError -> {
        log.warn { "Geo clint threw serverError, while processing location" }
      }

      is Failure.UnspecifiedError -> {
        log.error(failure.exception) { "Geo client threw unspecified error, while processing location" }
      }
    }
  }
}