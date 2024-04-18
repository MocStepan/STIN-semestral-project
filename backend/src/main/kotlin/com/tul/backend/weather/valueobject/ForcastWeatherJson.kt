package com.tul.backend.weather.valueobject

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class ForecastWeatherJson(
  @JsonProperty("daily")
  val daily: HourlyDataJson
)

data class HourlyDataJson(
  @JsonProperty("time")
  val time: List<LocalDate>,
  @JsonProperty("temperature_2m_max")
  val maxTemperature: List<Double>,
  @JsonProperty("temperature_2m_min")
  val minTemperature: List<Double>,
  @JsonProperty("wind_speed_10m_max")
  val maxWindSpeed: List<Double>,
)
