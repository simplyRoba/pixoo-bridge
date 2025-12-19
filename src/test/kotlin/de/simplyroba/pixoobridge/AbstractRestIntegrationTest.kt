package de.simplyroba.pixoobridge

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.client.RestTestClient
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
@EnableWireMock(
  ConfigureWireMock(name = "pixoo-led-matrix", baseUrlProperties = ["pixoo.base-url"])
)
abstract class AbstractRestIntegrationTest {

  @Autowired protected lateinit var restTestClient: RestTestClient

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

  protected fun doPostCallExpectingSuccess(path: String): RestTestClient.ResponseSpec =
    restTestClient
      .post()
      .uri(path)
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .is2xxSuccessful

  protected fun doPostCallWithBodyExpectingSuccess(
    path: String,
    body: Any,
  ): RestTestClient.ResponseSpec = doPostCallWithBody(path, body).expectStatus().is2xxSuccessful()

  protected fun doPostCallWithBody(path: String, body: Any): RestTestClient.ResponseSpec =
    restTestClient
      .post()
      .uri(path)
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .body(body)
      .exchange()

  protected fun doGetCallExpectingSuccess(path: String): RestTestClient.ResponseSpec =
    doGetCall(path).expectStatus().is2xxSuccessful

  protected fun doGetCall(path: String): RestTestClient.ResponseSpec =
    restTestClient.get().uri(path).accept(MediaType.APPLICATION_JSON).exchange()

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
