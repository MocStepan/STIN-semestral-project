package com.tul.backend.weather.repository

import com.tul.backend.weather.entity.UserWeatherLocation
import org.springframework.data.jpa.repository.JpaRepository

interface UserWeatherLocationRepository : JpaRepository<UserWeatherLocation, Long> {

  fun findByUser_Id(id: Long): List<UserWeatherLocation>

  fun existsByUser_IdAndLocation(id: Long, location: String): Boolean

  fun existsByUser_IdAndId(id: Long, id1: Long): Boolean
}
