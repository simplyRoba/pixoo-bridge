package de.simplyroba.pixoobridge.client

import de.simplyroba.pixoobridge.config.PixooConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PixooDeviceClient(private val config: PixooConfig) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private final val client: WebClient = WebClient.create("http://${config.host}")

    fun screenSwitch()

}