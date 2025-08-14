plugins {
  kotlin("jvm") version libs.versions.kotlin.core
  kotlin("plugin.spring") version libs.versions.kotlin.core
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependency.management)
  alias(libs.plugins.spottless)
}

group = "de.simplyroba"

version = file("version.txt").readText().trim()

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

repositories { mavenCentral() }

val springCloudVersion = "2025.0.0"

// security version bumps through spring dependency management
// will not be updated through dependabot
// like extra["libXX.version"] = "XXX"

// direkt security version bumps
val guavaVersion = "33.4.8-jre"

dependencies {
  implementation(kotlin("reflect"))
  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.boot.starter.webflux) // TODO switch to RestClient
  implementation(libs.jackson.module.kotlin)
  implementation(libs.springdoc.openapi.webmvc)
  implementation(libs.scrimage.core)

  testImplementation(libs.spring.boot.starter.test)
  testImplementation(libs.mockito.kotlin) // TODO use springmockk
  testImplementation(libs.spring.cloud.stub.runner) // TODO use wiremock spring boot

  constraints {
    implementation(libs.guava) {
      version { strictly(guavaVersion) }
      because("https://github.com/simplyRoba/pixoo-bridge/security/dependabot/22")
      because("https://github.com/simplyRoba/pixoo-bridge/security/dependabot/21")
    }
  }
}

dependencyManagement {
  imports { mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.spring.cloud}") }
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
    leadingTabsToSpaces(2)
  }

  kotlin {
    target("src/**/*.kt", "src/**/*.kts")
    targetExclude("build/**/*.kts", "build/**/*.kt")
    ktfmt().googleStyle()
    leadingTabsToSpaces(2)
  }

  kotlinGradle {
    target("*.gradle.kts")
    targetExclude("build/**/*.gradle.kts")
    ktfmt().googleStyle()
  }

  freshmark { target("*.md") }
}
