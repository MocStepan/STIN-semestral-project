package com.tul.backend.weather.dto

import com.tul.backend.weather.valueobject.LocationOutcomeJson

data class LocationDTO(
  var latitude: Double,
  var longitude: Double
) {

  companion object {
    fun from(locationOutcomeJson: LocationOutcomeJson): LocationDTO {
      return LocationDTO(
        latitude = locationOutcomeJson.lat,
        longitude = locationOutcomeJson.lon
      )
    }
  }
}