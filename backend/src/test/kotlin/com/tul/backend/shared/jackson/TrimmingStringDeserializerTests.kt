package com.tul.backend.shared.jackson

import com.tul.backend.objectMapper
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class TrimmingStringDeserializerTest : FeatureSpec({

  feature("deserialize") {
    scenario("deserialize should trim the string") {
      val jsonString = """{"value":"   test   "}"""

      val result = objectMapper.readValue(jsonString, TestDTO::class.java)

      result.value shouldBe "test"
    }
  }

  feature("serialize") {
    scenario("serialize should not trim the string") {
      val expectedResult = """{"value":"   test   "}"""
      val test = TestDTO("   test   ")

      val result = objectMapper.writeValueAsString(test)

      result shouldBe expectedResult
    }
  }
})

private data class TestDTO(val value: String)