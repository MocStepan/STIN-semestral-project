package com.tul.backend.weather.service.client

import com.tul.backend.IntegrationTestApplication
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient

@Ignored
@SpringBootTest(classes = [IntegrationTestApplication::class])
class GeoDecoderClientServiceTests : FunSpec({

  test("getLocation should return locationJson") {
    val webClient = mockk<WebClient>()
    val webClientBuilder = mockk<WebClient.Builder>()
    val geoDecoderClientService = GeoDecoderClientService(webClientBuilder)

    every { webClientBuilder.clientConnector(any()) } returns webClientBuilder
    every { webClientBuilder.codecs(any()) } returns webClientBuilder
    every { webClientBuilder.baseUrl("https://geocoding-api.open-meteo.com/v1/search") } returns webClientBuilder
    every { webClientBuilder.build() } returns webClient


    val locationJson = geoDecoderClientService.getLocation("location")

    locationJson shouldBe "locationJson"
  }
})
