package com.tul.backend.weather.service.extractor

import com.tul.backend.weather.dto.LocationDTO
import com.tul.backend.weather.service.client.OpenMeteoClientService
import com.tul.backend.weather.valueobject.WeatherStatus.FORECAST
import com.tul.backend.weather.valueobject.WebClientOutcome
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class ForecastWeatherExtractorImplTests : FeatureSpec({

  feature("getWeatherJson") {
    scenario("should return forecast weather json") {
      val spec = getSpec()
      val location = "Liberec"
      val locationDTO = LocationDTO(
        latitude = 50.7702648,
        longitude = 15.0583947
      )
      val webClientOutcome = WebClientOutcome.Success("locationJson")

      coEvery { spec.locationExtractor.getLocation(location) } returns locationDTO
      coEvery { spec.openMeteoClientService.getForecastWeather(locationDTO) } returns webClientOutcome

      val result = spec.forecastWeatherExtractorImpl.getWeatherJson(location)

      result shouldBe webClientOutcome.response
      spec.forecastWeatherExtractorImpl.weatherStatus shouldBe FORECAST
    }

    scenario("location not found") {
      val spec = getSpec()
      val location = "Liberec"

      coEvery { spec.locationExtractor.getLocation(location) } returns null

      val result = spec.forecastWeatherExtractorImpl.getWeatherJson(location)

      result shouldBe null
      coVerify(exactly = 0) { spec.openMeteoClientService.getForecastWeather(any()) }
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
      coEvery { spec.openMeteoClientService.getForecastWeather(locationDTO) } returns webClientOutcome

      val result = spec.forecastWeatherExtractorImpl.getWeatherJson(location)

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
      coEvery { spec.openMeteoClientService.getForecastWeather(locationDTO) } returns webClientOutcome

      val result = spec.forecastWeatherExtractorImpl.getWeatherJson(location)

      result shouldBe null
    }
  }
})

private class ForecastWeatherExtractorImplSpecWrapper(
  val locationExtractor: LocationExtractor,
  val openMeteoClientService: OpenMeteoClientService
) {
  val forecastWeatherExtractorImpl = ForecastWeatherExtractorImpl(locationExtractor, openMeteoClientService)
}

private fun getSpec() = ForecastWeatherExtractorImplSpecWrapper(mockk(), mockk())