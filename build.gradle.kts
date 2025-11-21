plugins {
  kotlin("jvm") version libs.versions.kotlin.core
  kotlin("plugin.spring") version libs.versions.kotlin.core
  alias(libs.plugins.spring.dependency.management)
  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spottless)
}

group = "de.simplyroba"

version = file("version.txt").readText().trim()

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

repositories { mavenCentral() }

// security version bumps through spring dependency management
// will not be updated through dependabot
// like extra["libXX.version"] = "XXX"

dependencies {
  implementation(kotlin("reflect"))
  implementation(libs.spring.boot.starter.webmvc)
  implementation(libs.jackson.module.kotlin)
  implementation(libs.springdoc.openapi.webmvc)
  // TODO only for web client replace with rest client see #270
  implementation(libs.spring.boot.starter.webflux)
  implementation(libs.scrimage.core)

  testImplementation(libs.spring.boot.starter.webmvc.test)
  testImplementation(libs.mockito.kotlin) // TODO use springmockk
  testImplementation(libs.wiremock.spring.boot)
  testRuntimeOnly(libs.junit.platform)
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
