package com.tul.backend.weather.service.client

import com.tul.backend.weather.valueobject.WebClientOutcome
import org.springframework.http.HttpStatus
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody
import reactor.netty.http.client.HttpClient

@Service
class GeoDecoderClientService(
  webClientBuilder: WebClient.Builder
) {

  private val webClient = webClientBuilder
    .clientConnector(ReactorClientHttpConnector(HttpClient.create()))
    .codecs { configurer ->
      configurer
        .defaultCodecs()
        .maxInMemorySize(4096 * 1024) // Listing detail html resource size is 2,6MB
    }.baseUrl("https://nominatim.openstreetmap.org/search").build()

  suspend fun getLocation(location: String): WebClientOutcome {
    return try {
      val response = webClient.get()
        .uri { uriBuilder ->
          uriBuilder
            .queryParam("q", location)
            .queryParam("format", "json")
            .queryParam("limit", 1)
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