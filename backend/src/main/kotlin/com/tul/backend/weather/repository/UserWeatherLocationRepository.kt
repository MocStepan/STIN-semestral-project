package com.tul.backend.weather.repository

import com.tul.backend.weather.entity.UserWeatherLocation
import org.springframework.data.jpa.repository.JpaRepository

interface UserWeatherLocationRepository : JpaRepository<UserWeatherLocation, Long>
