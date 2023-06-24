import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "3.1.1"
  id("io.spring.dependency-management") version "1.1.0"
  kotlin("jvm") version "1.8.22"
  kotlin("plugin.spring") version "1.8.22"
  id("com.diffplug.spotless") version "6.19.0"
}

group = "de.simplyroba"

version = "0.1.0"

java.sourceCompatibility = JavaVersion.VERSION_17

repositories { mavenCentral() }

extra["springCloudVersion"] = "2022.0.3"

// security version bumps
extra["snakeyaml.version"] =
  "2.0" // https://github.com/simplyRoba/pixoo-bridge/security/dependabot/6

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-webflux") // only for webclient
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("openapiVersion")}")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.mockito.kotlin:mockito-kotlin:${property("mockitoKotlinVersion")}")
  testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")

  constraints {
    implementation("org.apache.httpcomponents:httpclient:4.5.13") {
      because("https://github.com/simplyRoba/pixoo-bridge/security/dependabot/4")
    }
    implementation("com.google.guava:guava:32.0.1-jre") {
      because("https://github.com/simplyRoba/pixoo-bridge/security/dependabot/7")
    }
  }
}

dependencyManagement {
  imports {
    mavenBom(
      "org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}"
    )
  }
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
