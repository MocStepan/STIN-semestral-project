package com.tul.backend.weather.valueobject

import com.fasterxml.jackson.annotation.JsonProperty


data class LocationOutcomeJsonList(
  val results: List<LocationOutcomeJson>
)

data class LocationOutcomeJson(
  @JsonProperty("latitude")
  val latitude: Double,
  @JsonProperty("longitude")
  val longitude: Double
)
