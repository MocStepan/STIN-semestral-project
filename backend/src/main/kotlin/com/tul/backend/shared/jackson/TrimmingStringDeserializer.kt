package com.tul.backend.shared.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import org.springframework.boot.jackson.JsonComponent

@JsonComponent
class TrimmingStringDeserializer : StringDeserializer() {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): String {
    val value = p.valueAsString
    return value.trim()
  }
}