package com.tul.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(IntegrationTestConfig::class)
class IntegrationTestApplication

fun main(args: Array<String>) {
  runApplication<IntegrationTestApplication>(*args)
}
