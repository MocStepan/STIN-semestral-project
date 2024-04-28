package com.tul.backend.weather.dto

import com.tul.backend.weather.valueobject.LocationOutcomeJson

data class LocationDTO(
  val latitude: Double,
  val longitude: Double
) {

  companion object {
    fun from(locationOutcomeJson: LocationOutcomeJson): LocationDTO {
      return LocationDTO(
        latitude = locationOutcomeJson.latitude,
        longitude = locationOutcomeJson.longitude
      )
    }
  }
}
