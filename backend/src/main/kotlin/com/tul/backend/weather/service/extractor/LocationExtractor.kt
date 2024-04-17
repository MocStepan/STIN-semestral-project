package com.tul.backend.weather.service.extractor

import com.tul.backend.weather.dto.LocationDTO

interface LocationExtractor {

  suspend fun getLocation(location: String): LocationDTO?
}