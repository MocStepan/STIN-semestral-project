package com.tul.backend.weather.service.client

import com.tul.backend.IntegrationTestApplication
import com.tul.backend.weather.valueobject.WebClientOutcome
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import java.net.URI

@Ignored
@SpringBootTest(classes = [IntegrationTestApplication::class])
class GeoDecoderClientServiceTests : FunSpec({

  val test = mockk<WebClient>()

  @MockK
  lateinit var webClient: WebClient

  @MockK
  lateinit var requestBodyUriSpec: WebClient.RequestBodyUriSpec

  @MockK
  lateinit var requestBodySpec: WebClient.RequestBodySpec

  @MockK
  lateinit var responseSpec: WebClient.ResponseSpec

  @InjectMockKs
  lateinit var geoDecoderClientService: GeoDecoderClientService

  test("getLocation should return locationJson") {
    val location = "Berlin"

    every { webClient.get() } returns requestBodyUriSpec
    every { requestBodyUriSpec.uri(any<java.util.function.Function<UriBuilder, URI>>()) } returns requestBodySpec
    every { requestBodySpec.retrieve() } returns responseSpec

    val result = geoDecoderClientService.getLocation(location)

    result shouldBe WebClientOutcome.Success::class
  }
})
