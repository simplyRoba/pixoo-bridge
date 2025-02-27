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
        .trimIndent(),
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
      .is2xxSuccessful

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
  fun `should send remote image`() {
    val picId = 11
    val imagePath = "/path-to-image/black"
    stubNextPictureIdCall(picId)

    // let the remote image be delivered by wiremock
    stubFor(
      get(imagePath)
        .willReturn(
          aResponse().withBody(ClassPathResource("images/black_100x100.jpg").contentAsByteArray)
        )
    )

    doPostCallWithBodyExpectingSuccess(
      "/draw/remote",
      """{"link": "${createFullWireMockUrl(imagePath)}"}""",
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
  fun `should return bad request if the url did point to anything but an image`() {
    val notAnImagePath = "/not-an-image"

    // let the remote file be delivered by wiremock
    stubFor(
      get(notAnImagePath)
        .willReturn(aResponse().withBody(ClassPathResource("images/text.txt").contentAsByteArray))
    )

    doPostCallWithBody("/draw/remote", """{"link": "${createFullWireMockUrl(notAnImagePath)}"}""")
      .expectStatus()
      .isBadRequest
  }

  @Test
  fun `should return not found if the url did point to nothing downloadable`() {
    val nothingPath = "/points-to-nothing"

    stubFor(get(nothingPath).willReturn(aResponse()))

    doPostCallWithBody("/draw/remote", """{"link": "${createFullWireMockUrl(nothingPath)}"}""")
      .expectStatus()
      .isNotFound
  }

  @Test
  fun `should return not found if the url return anything but a 200`() {
    val notFoundPath = "/points-to-nothing"

    stubFor(get(notFoundPath).willReturn(aResponse().withBody("Something").withStatus(404)))

    doPostCallWithBody("/draw/remote", """{"link": "${createFullWireMockUrl(notFoundPath)}"}""")
      .expectStatus()
      .isNotFound
  }

  @Test
  fun `should send text command`() {
    doPostCallWithBodyExpectingSuccess(
      "/draw/text",
      """
        {
          "id":4,
          "position": {
            "x":0,
            "y":40
          },
          "scrollDirection":"LEFT",
          "font":4,
          "textWidth":56,
          "text":"hello, world",
          "scrollSpeed": 10,
          "color": {
            "red":255,
            "green":255,
            "blue":0
          },
          "textAlignment":"LEFT"
        }
        """
        .trimIndent(),
    )
    verifyCommandSent(
      """
        {
          "Command":"Draw/SendHttpText",
          "TextId":4,
          "x":0,
          "y":40,
          "dir":0,
          "font":4,
          "TextWidth":56,
          "speed":10,
          "TextString":"hello, world",
          "color":"#FFFF00",
          "align":1
        }
        """
        .trimIndent()
    )
  }

  @Test
  fun `should send clear text command`() {
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
