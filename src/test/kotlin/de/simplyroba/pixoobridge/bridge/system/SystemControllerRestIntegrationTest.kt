package de.simplyroba.pixoobridge.bridge.system

import com.github.tomakehurst.wiremock.client.WireMock.*
import de.simplyroba.pixoobridge.AbstractRestIntegrationTest
import org.junit.jupiter.api.Test

class SystemControllerRestIntegrationTest : AbstractRestIntegrationTest() {

  @Test
  fun `should include pixoo in health check`() {
    stubFor(get(urlEqualTo("/get")).willReturn(aResponse()))
    doGetCallExpectingSuccess("/health/check")
    verify(getRequestedFor(urlEqualTo("/get")))
  }

  @Test
  fun `should fail if pixoo does not return success`() {
    stubFor(get(urlEqualTo("/get")).willReturn(aResponse().withStatus(500)))
    doGetCall("/health/check").expectStatus().is5xxServerError
  }

  @Test
  fun `should send reboot command`() {
    doPostCallExpectingSuccess("/reboot")
    verifyCommandSent("""{"Command":"Device/SysReboot"}""")
  }
}
