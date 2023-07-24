package de.simplyroba.pixoobridge.bridge.draw

import com.github.tomakehurst.wiremock.client.WireMock.*
import de.simplyroba.pixoobridge.AbstractRestIntegrationTest
import de.simplyroba.test.JsonUnitRegexBuilder.Companion.regex
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.reactive.function.BodyInserters

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
          "PicSpeed": 9999,
          "PicData": ${regex().exp("A{16384}")} 
        }
        """
        .trimIndent()
    )
  }

  @Test
  fun `should send single image`() {
    val picId = 12
    stubNextPictureIdCall(picId)

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder
      .part("image", ClassPathResource("images/black_100x100.jpg"))
      .contentType(MediaType.MULTIPART_FORM_DATA)

    webTestClient
      .post()
      .uri("/draw/upload")
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .is2xxSuccessful()

    verifyCommandSent(
      """
        {
          "Command": "Draw/SendHttpGif",
          "PicNum": 1,
          "PicWidth": 64,
          "PicOffset": 0,
          "PicID": $picId,
          "PicSpeed": 9999,
          "PicData": ${regex().exp("A{16384}")} 
        }
        """
        .trimIndent()
    )
  }

  @Test
  fun `should send each frame of animated gif`() {
    val picId = 32
    stubNextPictureIdCall(picId)

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder
      .part("image", ClassPathResource("images/black_white_animated_100x100_200ms.gif"))
      .contentType(MediaType.MULTIPART_FORM_DATA)

    webTestClient
      .post()
      .uri("/draw/upload")
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .is2xxSuccessful()

    verifyCommandSent(
      """
        {
          "Command": "Draw/SendHttpGif",
          "PicNum": 2,
          "PicWidth": 64,
          "PicOffset": 0,
          "PicID": $picId,
          "PicSpeed": 200,
          "PicData": ${regex().exp("A{16384}")} 
        }
        """
        .trimIndent()
    )

    verifyCommandSent(
      """
        {
          "Command": "Draw/SendHttpGif",
          "PicNum": 2,
          "PicWidth": 64,
          "PicOffset": 1,
          "PicID": $picId,
          "PicSpeed": 200,
          "PicData": ${regex().exp("\\/{16384}")} 
        }
        """
        .trimIndent()
    )
  }

  @Test
  fun `should send correct delay for each frame of animated gif`() {
    val picId = 67
    stubNextPictureIdCall(picId)

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder
      .part("image", ClassPathResource("images/black_white_animated_100x100_1000ms.gif"))
      .contentType(MediaType.MULTIPART_FORM_DATA)

    webTestClient
      .post()
      .uri("/draw/upload")
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .is2xxSuccessful()

    verifyCommandSent(
      """
        {
          "Command": "Draw/SendHttpGif",
          "PicNum": 2,
          "PicWidth": 64,
          "PicOffset": 0,
          "PicID": $picId,
          "PicSpeed": 1000,
          "PicData": ${regex().exp("A{16384}")} 
        }
        """
        .trimIndent()
    )

    verifyCommandSent(
      """
        {
          "Command": "Draw/SendHttpGif",
          "PicNum": 2,
          "PicWidth": 64,
          "PicOffset": 1,
          "PicID": $picId,
          "PicSpeed": 1000,
          "PicData": ${regex().exp("\\/{16384}")} 
        }
        """
        .trimIndent()
    )
  }

  @Test
  fun `should show text`() {

  }

  @Test
  fun `should clear text`() {
    doPostCallExpectingSuccess("/draw/text/clear")
    verifyCommandSent("""{"Command":"Draw/ClearHttpText"}""")
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
