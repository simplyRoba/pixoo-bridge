package de.simplyroba.pixoobridge.bridge.health

import com.github.tomakehurst.wiremock.client.WireMock.*
import de.simplyroba.pixoobridge.AbstractRestIntegrationTest
import org.junit.jupiter.api.Test

class HealthControllerRestIntegrationTest : AbstractRestIntegrationTest() {

  @Test
  fun `should include pixoo in health check`() {
    stubFor(get(urlEqualTo("/get")).willReturn(aResponse()))
    doGetCall("/health/check")
    verify(getRequestedFor(urlEqualTo("/get")))
  }
}
