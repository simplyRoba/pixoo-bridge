package de.simplyroba.pixoobridge.config

import de.simplyroba.pixoobridge.AbstractRestIntegrationTest
import org.junit.jupiter.api.Test

class SwaggerRedirectIntegrationTest : AbstractRestIntegrationTest() {

  @Test
  fun shouldReturnApiDocsContainingMinimalConfig() {
    restTestClient
      .get()
      .uri("/v3/api-docs")
      .exchange()
      .expectStatus()
      .is2xxSuccessful
      .expectBody()
      .jsonPath("paths")
      .isNotEmpty
      .jsonPath("servers")
      .isNotEmpty
      .jsonPath("components")
      .isNotEmpty
  }

  @Test
  fun shouldGenerateSwaggerUi() {
    restTestClient.get().uri("swagger-ui/index.html").exchange().expectStatus().is2xxSuccessful
  }
}
