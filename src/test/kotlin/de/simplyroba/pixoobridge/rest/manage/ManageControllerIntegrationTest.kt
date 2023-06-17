package de.simplyroba.pixoobridge.rest.manage

import com.github.tomakehurst.wiremock.client.WireMock.*
import de.simplyroba.pixoobridge.AbstractIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.web.reactive.server.WebTestClient

class ManageControllerIntegrationTest: AbstractIntegrationTest() {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun shouldTurnOnDisplay() {

        stubFor(post(urlEqualTo("/post"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error_code\":0}")))

        webTestClient
            .post()
            .uri("/manage/display/on/true")
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()

        verify(postRequestedFor(urlEqualTo("/post"))
            .withHeader("Content-Type", equalTo(APPLICATION_JSON_VALUE))
            .withRequestBody(equalToJson("{\"Command\":\"Channel/OnOffScreen\", \"OnOff\": 1}")))
    }
}