import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.9.23"
  kotlin("plugin.spring") version "1.9.23"
  id("org.springframework.boot") version "3.2.5"
  id("io.spring.dependency-management") version "1.1.4"
  id("com.diffplug.spotless") version "6.25.0"
}

group = "de.simplyroba"

version = "0.5.4"

java.sourceCompatibility = JavaVersion.VERSION_21

repositories { mavenCentral() }

val springCloudVersion = "2023.0.1"
val openapiVersion = "2.5.0"
val mockitoKotlinVersion = "5.3.1"
val scrimageVersion = "4.1.3"

// security version bumps through spring dependency management
// will not be updated through dependabot
// like extra["libXX.version"] = "XXX"

// direkt security version bumps
val guavaVersion = "33.2.0-jre"

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
    jvmTarget = "21"
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
