package de.simplyroba.pixoobridge

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
abstract class AbstractRestIntegrationTest {

  @Autowired lateinit var webTestClient: WebTestClient

  @Value("\${wiremock.server.port}") lateinit var wireMockPort: String

  @BeforeEach
  fun defaultSuccessResponseStub() {
    reset()
    stubFor(
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
      .is2xxSuccessful()

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
    doGetCall(path).expectStatus().is2xxSuccessful()

  protected fun doGetCall(path: String) =
    webTestClient.get().uri(path).accept(MediaType.APPLICATION_JSON).exchange()

  /**
   * Wiremock uses JsonUnit and therefore can handle placeholders. See:
   * https://wiremock.org/docs/request-matching/#json-equality and:
   * https://github.com/lukas-krecan/JsonUnit#typeplc
   */
  protected fun verifyCommandSent(commandJson: String) =
    verify(
      postRequestedFor(urlEqualTo("/post"))
        .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
        .withRequestBody(equalToJson(commandJson))
    )

  protected fun createFullWireMockUrl(path: String) = "http://localhost:$wireMockPort$path"
}
