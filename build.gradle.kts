import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "3.1.2"
  id("io.spring.dependency-management") version "1.1.2"
  kotlin("jvm") version "1.9.0"
  kotlin("plugin.spring") version "1.9.0"
  id("com.diffplug.spotless") version "6.20.0"
}

group = "de.simplyroba"

version = "0.2.0"

java.sourceCompatibility = JavaVersion.VERSION_17

repositories { mavenCentral() }

val springCloudVersion = "2022.0.4"
val openapiVersion = "2.2.0"
val mockitoKotlinVersion = "5.0.0"
val scrimageVersion = "4.0.38"

// security version bumps through spring dependency management
// will not be updated through dependabot
// https://github.com/simplyRoba/pixoo-bridge/security/dependabot/6
extra["snakeyaml.version"] = "2.0"
// direkt security version bumps
val httpClientVersion = "4.5.14"
val guavaVersion = "32.1.1-jre"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-webflux") // only for webclient
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openapiVersion")
  implementation("com.sksamuel.scrimage:scrimage-core:$scrimageVersion")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
  testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")

  constraints {
    implementation("org.apache.httpcomponents:httpclient:$httpClientVersion") {
      because("https://github.com/simplyRoba/pixoo-bridge/security/dependabot/4")
    }
    implementation("com.google.guava:guava:$guavaVersion") {
      because("https://github.com/simplyRoba/pixoo-bridge/security/dependabot/7")
    }
  }
}

dependencyManagement {
  imports { mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion") }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging { showStandardStreams = true }
}

spotless {
  format("misc") {
    target("**/*.yml", "**/*.yaml", "**/.gitignore")
    targetExclude("build/**", ".idea/**", ".gradle/**")
    trimTrailingWhitespace()
    endWithNewline()
    indentWithSpaces(2)
  }

  kotlin {
    target("src/**/*.kt", "src/**/*.kts")
    targetExclude("build/**/*.kts", "build/**/*.kt")
    ktfmt().googleStyle()
    indentWithSpaces(2)
  }

  kotlinGradle {
    target("*.gradle.kts")
    targetExclude("build/**/*.gradle.kts")
    ktfmt().googleStyle()
  }

  freshmark { target("*.md") }
}
