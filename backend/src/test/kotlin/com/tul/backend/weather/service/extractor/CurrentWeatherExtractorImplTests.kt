package com.tul.backend.weather.service.extractor

import com.tul.backend.weather.dto.LocationDTO
import com.tul.backend.weather.service.client.OpenMeteoClientService
import com.tul.backend.weather.valueobject.WeatherStatus.CURRENT
import com.tul.backend.weather.valueobject.WebClientOutcome
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class CurrentWeatherExtractorImplTests : FeatureSpec({

  feature("getWeatherJson") {
    scenario("should return current weather json") {
      val spec = getSpec()
      val location = "Liberec"
      val locationDTO = LocationDTO(
        latitude = 50.7702648,
        longitude = 15.0583947
      )
      val webClientOutcome = WebClientOutcome.Success("locationJson")

      coEvery { spec.locationExtractor.getLocation(location) } returns locationDTO
      coEvery { spec.openMeteoClientService.getCurrentWeather(locationDTO) } returns webClientOutcome

      val result = spec.currentWeatherExtractorImpl.getWeatherJson(location)

      result shouldBe webClientOutcome.response
      spec.currentWeatherExtractorImpl.weatherStatus shouldBe CURRENT
    }

    scenario("location not found") {
      val spec = getSpec()
      val location = "Liberec"

      coEvery { spec.locationExtractor.getLocation(location) } returns null

      val result = spec.currentWeatherExtractorImpl.getWeatherJson(location)

      result shouldBe null
      coVerify(exactly = 0) { spec.openMeteoClientService.getCurrentWeather(any()) }
    }

    scenario("web client server error") {
      val spec = getSpec()
      val location = "Liberec"
      val locationDTO = LocationDTO(
        latitude = 50.7702648,
        longitude = 15.0583947
      )
      val webClientOutcome = WebClientOutcome.Failure.ServerError

      coEvery { spec.locationExtractor.getLocation(location) } returns locationDTO
      coEvery { spec.openMeteoClientService.getCurrentWeather(locationDTO) } returns webClientOutcome

      val result = spec.currentWeatherExtractorImpl.getWeatherJson(location)

      result shouldBe null
    }

    scenario("web client unspecified error") {
      val spec = getSpec()
      val location = "Liberec"
      val locationDTO = LocationDTO(
        latitude = 50.7702648,
        longitude = 15.0583947
      )
      val webClientOutcome = WebClientOutcome.Failure.UnspecifiedError(Exception("error"))

      coEvery { spec.locationExtractor.getLocation(location) } returns locationDTO
      coEvery { spec.openMeteoClientService.getCurrentWeather(locationDTO) } returns webClientOutcome

      val result = spec.currentWeatherExtractorImpl.getWeatherJson(location)

      result shouldBe null
    }
  }
})

private class CurrentWeatherExtractorImplSpecWrapper(
  val locationExtractor: LocationExtractor,
  val openMeteoClientService: OpenMeteoClientService
) {
  val currentWeatherExtractorImpl = CurrentWeatherExtractorImpl(locationExtractor, openMeteoClientService)
}

private fun getSpec() = CurrentWeatherExtractorImplSpecWrapper(mockk(), mockk())

