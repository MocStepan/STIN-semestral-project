package com.tul.backend.weather.service.client

import com.tul.backend.weather.dto.LocationDTO
import com.tul.backend.weather.valueobject.WebClientOutcome
import org.springframework.http.HttpStatus
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody
import reactor.netty.http.client.HttpClient

@Service
class OpenMeteoClientService {

  private val webClient = WebClient.builder()
    .clientConnector(ReactorClientHttpConnector(HttpClient.create()))
    .codecs { configurer ->
      configurer
        .defaultCodecs()
        .maxInMemorySize(4096 * 1024) // Listing detail html resource size is 2,6MB
    }.baseUrl("https://api.open-meteo.com/v1/forecast").build()

  // https://api.open-meteo.com/v1/forecast?latitude=50.7671&longitude=15.0562&daily=temperature_2m_max,temperature_2m_min,wind_speed_10m_max,wind_gusts_10m_max&timezone=auto&past_days=7
  suspend fun getForecastWeather(locationDTO: LocationDTO): WebClientOutcome {
    return try {
      val response = webClient.get()
        .uri { uriBuilder ->
          uriBuilder
            .queryParam("latitude", locationDTO.latitude)
            .queryParam("longitude", locationDTO.longitude)
            .queryParam("daily", "temperature_2m_max,temperature_2m_min,wind_speed_10m_max")
            .queryParam("timezone", "auto")
            .queryParam("past_days", 7)
            .build()
        }
        .retrieve()
        .awaitBody<String>()

      WebClientOutcome.Success(response)
    } catch (e: WebClientResponseException) {
      when (e.statusCode) {
        HttpStatus.SERVICE_UNAVAILABLE -> WebClientOutcome.Failure.ServerError
        else -> WebClientOutcome.Failure.UnspecifiedError(e)
      }
    }
  }

  // https://api.open-meteo.com/v1/forecast?latitude=50.7671&longitude=15.0562&current=temperature_2m,is_day,cloud_cover,wind_speed_10m&timezone=auto&forecast_days=1
  suspend fun getCurrentWeather(locationDTO: LocationDTO): WebClientOutcome {
    return try {
      val response = webClient.get()
        .uri { uriBuilder ->
          uriBuilder
            .queryParam("latitude", locationDTO.latitude)
            .queryParam("longitude", locationDTO.longitude)
            .queryParam("current", "temperature_2m,is_day,cloud_cover,wind_speed_10m")
            .queryParam("timezone", "auto")
            .queryParam("forecast_days", 1)
            .build()
        }
        .retrieve()
        .awaitBody<String>()

      WebClientOutcome.Success(response)
    } catch (e: WebClientResponseException) {
      when (e.statusCode) {
        HttpStatus.SERVICE_UNAVAILABLE -> WebClientOutcome.Failure.ServerError
        else -> WebClientOutcome.Failure.UnspecifiedError(e)
      }
    }
  }
}