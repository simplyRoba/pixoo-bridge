package de.simplyroba.pixoobridge

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@EnableWireMock(
  ConfigureWireMock(name = "pixoo-led-matrix", baseUrlProperties = ["pixoo.base-url"])
)
abstract class AbstractRestIntegrationTest {

  @Autowired protected lateinit var webTestClient: WebTestClient

  @InjectWireMock("pixoo-led-matrix") protected lateinit var pixooLedMatrix: WireMockServer

  @BeforeEach
  fun defaultSuccessResponseStub() {
    pixooLedMatrix.resetAll()
    pixooLedMatrix.stubFor(
      post(urlEqualTo("/post"))
        .willReturn(
          aResponse().withHeader("Content-Type", "text/html").withBody("{\"error_code\":0}")
        )
    )
  }

  protected fun doPostCallExpectingSuccess(path: String) =
    webTestClient
      .post()
      .uri(path)
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .is2xxSuccessful

  protected fun doPostCallWithBodyExpectingSuccess(path: String, body: Any) =
    doPostCallWithBody(path, body).expectStatus().is2xxSuccessful()

  protected fun doPostCallWithBody(path: String, body: Any) =
    webTestClient
      .post()
      .uri(path)
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .body(BodyInserters.fromValue(body))
      .exchange()

  protected fun doGetCallExpectingSuccess(path: String) =
    doGetCall(path).expectStatus().is2xxSuccessful

  protected fun doGetCall(path: String) =
    webTestClient.get().uri(path).accept(MediaType.APPLICATION_JSON).exchange()

  /**
   * Wiremock uses JsonUnit and therefore can handle placeholders. See:
   * https://wiremock.org/docs/request-matching/#json-equality and:
   * https://github.com/lukas-krecan/JsonUnit#typeplc
   */
  protected fun verifyCommandSent(commandJson: String) =
    pixooLedMatrix.verify(
      postRequestedFor(urlEqualTo("/post"))
        .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withRequestBody(equalToJson(commandJson))
    )

  protected fun createFullWireMockUrl(path: String) = "${pixooLedMatrix.baseUrl()}$path"
}
