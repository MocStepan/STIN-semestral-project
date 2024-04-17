package com.tul.backend.weather.valueobject

sealed class WebClientOutcome {
  data class Success(val response: String) : WebClientOutcome()

  sealed class Failure : WebClientOutcome() {
    data object ServerError : Failure()
    data class UnspecifiedError(val exception: Exception) : Failure()
  }
}