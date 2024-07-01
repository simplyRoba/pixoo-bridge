package de.simplyroba.pixoobridge

import org.junit.jupiter.api.Test

class SwaggerIntegrationTest : AbstractRestIntegrationTest() {

  @Test
  fun shouldReturnApiDocsContainingMinimalConfig() {
    webTestClient
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
    webTestClient.get().uri("swagger-ui/index.html").exchange().expectStatus().is2xxSuccessful
  }
}
