package com.tul.backend.weather.valueobject

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class CurrentWeatherJson(
  @JsonProperty("current")
  val current: Json
)

data class Json(
  @JsonProperty("time")
  val time: LocalDateTime,
  @JsonProperty("temperature_2m")
  val temperature: Double,
  @JsonProperty("cloud_cover")
  val cloudCover: Int,
  @JsonProperty("is_day")
  val isDay: Int,
  @JsonProperty("wind_speed_10")
  val windSpeed: Double,
)