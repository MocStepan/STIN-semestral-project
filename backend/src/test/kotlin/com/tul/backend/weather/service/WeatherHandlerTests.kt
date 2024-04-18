package com.tul.backend.weather.service

import com.tul.backend.weather.dto.CurrentWeatherDTO
import com.tul.backend.weather.service.extractor.CurrentWeatherExtractorImpl
import com.tul.backend.weather.service.extractor.ForecastWeatherExtractorImpl
import com.tul.backend.weather.service.extractor.WeatherExtractor
import com.tul.backend.weather.service.parser.WebClientParser
import com.tul.backend.weather.valueobject.WeatherStatus
import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime

@Ignored
class WeatherHandlerTests : FeatureSpec({

  feature("getCurrentWeather") {
    scenario("should return current weather") {
      val spec = getSpec()
      val location = "location"
      val currentWeatherDTO = CurrentWeatherDTO(
        time = LocalDateTime.now(),
        temperature = 20.0,
        cloudCover = 50,
        windSpeed = 10.0,
        isDay = true
      )

      every { spec.weatherExtractor[0].weatherStatus } returns WeatherStatus.CURRENT
      every { spec.weatherExtractor[1].weatherStatus } returns WeatherStatus.FORECAST
      every { spec.weatherExtractor[0].getWeatherJson(location) } returns "weatherJson"
      every { spec.webClientParser.parseCurrentWeatherJson("weatherJson") } returns currentWeatherDTO

      val result = spec.weatherHandler.getCurrentWeather(location)

      result shouldBe currentWeatherDTO
    }

  }
})

private class WeatherHandlerSpecWrapper(
  var weatherExtractor: List<WeatherExtractor>,
  var webClientParser: WebClientParser
) {
  val weatherHandler = WeatherHandler(weatherExtractor, webClientParser)
}

private fun getSpec() = WeatherHandlerSpecWrapper(
  listOf(mockk<CurrentWeatherExtractorImpl>(), mockk<ForecastWeatherExtractorImpl>()),
  mockk()
)
