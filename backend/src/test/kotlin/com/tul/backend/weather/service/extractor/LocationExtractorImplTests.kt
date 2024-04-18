package com.tul.backend.weather.service.extractor

import com.tul.backend.weather.dto.LocationDTO
import com.tul.backend.weather.service.client.GeoDecoderClientService
import com.tul.backend.weather.service.parser.WebClientParser
import com.tul.backend.weather.valueobject.WebClientOutcome
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class LocationExtractorImplTests : FeatureSpec({

  feature("getWeatherJson") {
    scenario("should return forecast weather json") {
      val spec = getSpec()
      val location = "Liberec"
      val locationDTO = LocationDTO(
        latitude = 50.7702648,
        longitude = 15.0583947
      )
      val webClientOutcome = WebClientOutcome.Success("locationJson")

      coEvery { spec.geoDecoderClientService.getLocation(location) } returns webClientOutcome
      every { spec.webClientParser.parseLocationJson(webClientOutcome.response) } returns locationDTO

      val result = spec.forecastWeatherExtractorImpl.getLocation(location)!!

      result.latitude shouldBe locationDTO.latitude
      result.longitude shouldBe locationDTO.longitude
    }

    scenario("web client server error") {
      val spec = getSpec()
      val location = "Liberec"
      val webClientOutcome = WebClientOutcome.Failure.ServerError

      coEvery { spec.geoDecoderClientService.getLocation(location) } returns webClientOutcome

      val result = spec.forecastWeatherExtractorImpl.getLocation(location)

      result shouldBe null
      verify(exactly = 0) { spec.webClientParser.parseLocationJson(any()) }
    }

    scenario("web client unspecified error") {
      val spec = getSpec()
      val location = "Liberec"
      val webClientOutcome = WebClientOutcome.Failure.UnspecifiedError(Exception("test"))

      coEvery { spec.geoDecoderClientService.getLocation(location) } returns webClientOutcome

      val result = spec.forecastWeatherExtractorImpl.getLocation(location)

      result shouldBe null
      verify(exactly = 0) { spec.webClientParser.parseLocationJson(any()) }
    }
  }
})

private class LocationExtractorImplSpecWrapper(
  val geoDecoderClientService: GeoDecoderClientService,
  val webClientParser: WebClientParser
) {
  val forecastWeatherExtractorImpl = LocationExtractorImpl(geoDecoderClientService, webClientParser)
}

private fun getSpec() = LocationExtractorImplSpecWrapper(mockk(), mockk())