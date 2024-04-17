package com.tul.backend.weather.valueobject

import com.fasterxml.jackson.annotation.JsonProperty


data class LocationOutcomeJsonList(
  val location: List<LocationOutcomeJson>
)

data class LocationOutcomeJson(
  @JsonProperty("lat")
  val lat: Double,
  @JsonProperty("lon")
  val lon: Double
)
