package de.simplyroba.pixoobridge

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    companion object {
        @JvmStatic
        @BeforeAll
        fun defaultSuccessResponseStub() {
            stubFor(
                post(urlEqualTo("/post")).willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withBody("{\"error_code\":0}")
                )
            )
        }
    }

    protected fun callBridge(s: String) {
        webTestClient
            .post()
            .uri(s)
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
    }

    protected fun verifyCommand(s: String) {
        verify(
            postRequestedFor(urlEqualTo("/post"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson(s))
        )
    }
}