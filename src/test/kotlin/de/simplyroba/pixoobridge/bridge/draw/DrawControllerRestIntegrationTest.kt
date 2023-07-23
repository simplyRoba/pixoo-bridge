package de.simplyroba.pixoobridge.bridge.draw

import com.github.tomakehurst.wiremock.client.WireMock.*
import de.simplyroba.pixoobridge.AbstractRestIntegrationTest
import de.simplyroba.test.JsonUnitRegexBuilder.Companion.regex
import org.junit.jupiter.api.Test

class DrawControllerRestIntegrationTest : AbstractRestIntegrationTest() {

  @Test
  fun `should show single color`() {
    val picId = 42
    stubNextPictureIdCall(picId)
    doPostCallWithBodyExpectingSuccess(
      "/draw/fill",
      """
        {
          "red": 0,
          "green": 0,
          "blue": 0
        }
        """
        .trimIndent()
    )
    verifyCommandSent(
      """
        {
          "Command": "Draw/SendHttpGif",
          "PicNum": 1,
          "PicWidth": 64,
          "PicOffset": 0,
          "PicID": $picId,
          "PicSpeed": 1000,
          "PicData": ${regex().exp("A{16384}")} 
        }
        """
        .trimIndent()
    )
  }

  private fun stubNextPictureIdCall(picId: Int) {
    stubFor(
      post(urlEqualTo("/post"))
        .withRequestBody(equalToJson("{\"Command\":\"Draw/GetHttpGifId\"}"))
        .willReturn(
          aResponse()
            .withHeader("Content-Type", "text/html")
            .withBody("{\"error_code\": 0,\"PicId\":$picId}")
        )
    )
  }
}
