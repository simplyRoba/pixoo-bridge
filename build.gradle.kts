plugins {
  kotlin("jvm") version "2.1.10"
  kotlin("plugin.spring") version "2.1.10"
  id("org.springframework.boot") version "3.4.3"
  id("io.spring.dependency-management") version "1.1.7"
  id("com.diffplug.spotless") version "7.0.2"
}

group = "de.simplyroba"

version = file("version.txt").readText().trim()

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

repositories { mavenCentral() }

val springCloudVersion = "2024.0.0"
val openapiVersion = "2.8.5"
val mockitoKotlinVersion = "5.4.0"
val scrimageVersion = "4.3.0"

// security version bumps through spring dependency management
// will not be updated through dependabot
// like extra["libXX.version"] = "XXX"

// direkt security version bumps
val guavaVersion = "33.4.0-jre"

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
      because("https://github.com/simplyRoba/pixoo-bridge/security/dependabot/22")
      because("https://github.com/simplyRoba/pixoo-bridge/security/dependabot/21")
    }
  }
}

dependencyManagement {
  imports { mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion") }
}

kotlin { compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") } }

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
