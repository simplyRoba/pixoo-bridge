package de.simplyroba.pixoobridge.rest.manage

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/manage")
class ManageController {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/display/{mode}")
    fun manageDisplay(@PathVariable mode: String): Mono<ServerResponse> {

        logger.info("geil!")

        return ServerResponse.ok().build()
    }
}