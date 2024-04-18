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
    }.baseUrl("https://geocoding-api.open-meteo.com/v1/search").build()

  // https://geocoding-api.open-meteo.com/v1/search?name=%C4%8Cesk%C3%A9+Bud%C4%9Bjovice&count=1&language=en&format=json
  suspend fun getLocation(location: String): WebClientOutcome {
    return try {
      val response = webClient.get()
        .uri { uriBuilder ->
          uriBuilder
            .queryParam("name", location)
            .queryParam("count", "1")
            .queryParam("language", "en")
            .queryParam("format", "json")
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