package com.tul.backend.weather.service

import com.tul.backend.auth.repository.AuthUserRepository
import com.tul.backend.createAuthUser
import com.tul.backend.createUserClaims
import com.tul.backend.weather.dto.CurrentWeatherDTO
import com.tul.backend.weather.dto.ForecastWeatherDTO
import com.tul.backend.weather.entity.UserWeatherLocation
import com.tul.backend.weather.repository.UserWeatherLocationRepository
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
import java.time.LocalDateTime

class WeatherServiceTests : FeatureSpec({

  val userClaims = createUserClaims()

  feature("getCurrentWeather") {
    scenario("should return current weather") {
      val spec = getSpec()
      val location = "location"
      val currentWeatherDTO = CurrentWeatherDTO(
        time = LocalDateTime.now(),
        temperature = 20.0,
        cloudCover = 50,
        windSpeed = 10.0,
        isDay = true
      )

      every { spec.weatherHandler.getCurrentWeather(location) } returns currentWeatherDTO

      val result = spec.weatherService.getCurrentWeather(location)

      result shouldBe currentWeatherDTO
    }

    scenario("should return nul, when current weather is not found") {
      val spec = getSpec()
      val location = "location"

      every { spec.weatherHandler.getCurrentWeather(location) } returns null

      val result = spec.weatherService.getCurrentWeather(location)

      result shouldBe null
    }
  }

  feature("getForecastWeather") {

    scenario("should return forecast weather") {
      val spec = getSpec()
      val location = "location"
      val forecastWeatherDTO = ForecastWeatherDTO(
        time = listOf(LocalDate.now()),
        maxTemperature = listOf(20.0),
        minTemperature = listOf(10.0),
        maxWindSpeed = listOf(10.0),
      )

      every { spec.weatherHandler.getForecastWeather(location) } returns forecastWeatherDTO

      val result = spec.weatherService.getForecastWeather(location)

      result shouldBe forecastWeatherDTO
    }

    scenario("should return nul, when forecast weather is not found") {
      val spec = getSpec()
      val location = "location"

      every { spec.weatherHandler.getForecastWeather(location) } returns null

      val result = spec.weatherService.getForecastWeather(location)

      result shouldBe null
    }
  }

  feature("saveUserWeatherLocation") {
    scenario("should save user weather location") {
      val spec = getSpec()
      val location = "location"
      val authUser = createAuthUser()
      val userWeatherLocationSlot = slot<UserWeatherLocation>()

      every { spec.authUserRepository.findByIdOrNull(userClaims.authUserId) } returns authUser
      every { spec.userWeatherLocationRepository.existsByUser_IdAndLocation(authUser.id, location) } returns false
      every { spec.userWeatherLocationRepository.save(capture(userWeatherLocationSlot)) } returnsArgument 0

      val result = spec.weatherService.saveUserWeatherLocation(location, userClaims)

      result shouldBe true
      userWeatherLocationSlot.captured.location shouldBe location
      userWeatherLocationSlot.captured.user shouldBe authUser
    }

    scenario("should not save user weather location, when user not found") {
      val spec = getSpec()
      val location = "location"

      every { spec.authUserRepository.findByIdOrNull(userClaims.authUserId) } returns null

      val result = spec.weatherService.saveUserWeatherLocation(location, userClaims)

      result shouldBe false
      verify(exactly = 0) { spec.userWeatherLocationRepository.save(any()) }
    }

    scenario("should not save user weather location, when location already exists") {
      val spec = getSpec()
      val location = "location"
      val authUser = createAuthUser()

      every { spec.authUserRepository.findByIdOrNull(userClaims.authUserId) } returns authUser
      every { spec.userWeatherLocationRepository.existsByUser_IdAndLocation(authUser.id, location) } returns true

      val result = spec.weatherService.saveUserWeatherLocation(location, userClaims)

      result shouldBe false
      verify(exactly = 0) { spec.userWeatherLocationRepository.save(any()) }
    }
  }

  feature("getUserWeatherLocations") {
    scenario("should return user weather locations") {
      val spec = getSpec()
      val authUser = createAuthUser()
      val userWeatherLocation = UserWeatherLocation.from("location", authUser)

      every { spec.authUserRepository.findByIdOrNull(userClaims.authUserId) } returns authUser
      every { spec.userWeatherLocationRepository.findByUser_Id(authUser.id) } returns listOf(userWeatherLocation)

      val result = spec.weatherService.getUserWeatherLocations(userClaims)!!

      result.size shouldBe 1
      result.first().location shouldBe userWeatherLocation.location
    }

    scenario("should return empty list, when user not found") {
      val spec = getSpec()

      every { spec.authUserRepository.findByIdOrNull(userClaims.authUserId) } returns null

      val result = spec.weatherService.getUserWeatherLocations(userClaims)

      result shouldBe null
      verify(exactly = 0) { spec.userWeatherLocationRepository.findByUser_Id(any()) }
    }
  }

  feature("deleteUserWeatherLocation") {
    scenario("should delete user weather location") {
      val spec = getSpec()
      val id = 1L

      every { spec.userWeatherLocationRepository.existsByUser_IdAndId(userClaims.authUserId, id) } returns true
      every { spec.userWeatherLocationRepository.deleteById(id) } returnsArgument 0

      val result = spec.weatherService.deleteUserWeatherLocation(id, userClaims)

      result shouldBe true
    }

    scenario("should not delete user weather location, when location not found") {
      val spec = getSpec()
      val id = 1L

      every { spec.userWeatherLocationRepository.existsByUser_IdAndId(userClaims.authUserId, id) } returns false

      val result = spec.weatherService.deleteUserWeatherLocation(id, userClaims)

      result shouldBe false
      verify(exactly = 0) { spec.userWeatherLocationRepository.deleteById(1L) }
    }
  }
})

private class WeatherServiceSpecWrapper(
  val weatherHandler: WeatherHandler,
  val authUserRepository: AuthUserRepository,
  val userWeatherLocationRepository: UserWeatherLocationRepository
) {
  val weatherService = WeatherService(
    weatherHandler,
    authUserRepository,
    userWeatherLocationRepository
  )
}

private fun getSpec() = WeatherServiceSpecWrapper(mockk(), mockk(), mockk())