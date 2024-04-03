import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.regex.Pattern.compile

plugins {
  id("org.springframework.boot") version "3.2.4"
  id("io.spring.dependency-management") version "1.1.4"
  id("java")
  id("jacoco")
  kotlin("jvm") version "1.9.23"
  kotlin("plugin.spring") version "1.9.23"
  kotlin("plugin.jpa") version "1.9.23"
}

group = "com.tul"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-web-services")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  runtimeOnly("org.postgresql:postgresql")

  val kotestVersion = "5.8.0"
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")

  compile("org.springframework.boot:spring-boot-starter-webflux")
}


tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs += "-Xjsr305=strict"
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.test {
  finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
  reports {
    xml.required = true
  }
  dependsOn(tasks.test) // tests are required to run before generating the report
}
tasks.jacocoTestCoverageVerification {
  violationRules {
    rule {
      limit {
        minimum = BigDecimal("0.8")
      }
    }
  }
}

