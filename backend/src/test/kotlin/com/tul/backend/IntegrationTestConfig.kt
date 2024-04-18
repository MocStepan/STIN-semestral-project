package com.tul.backend

import com.ninjasquad.springmockk.MockkBean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class IntegrationTestConfig {

  @MockkBean(relaxed = true)
  lateinit var webClient: WebClient
}
