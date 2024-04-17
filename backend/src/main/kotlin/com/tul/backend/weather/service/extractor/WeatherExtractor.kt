package com.tul.backend.weather.service.extractor

import com.tul.backend.weather.valueobject.WeatherStatus

interface WeatherExtractor {

  val weatherStatus: WeatherStatus

  fun getWeatherJson(location: String): String?
}