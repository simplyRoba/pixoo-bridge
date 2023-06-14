package de.simplyroba.pixoobridge.rest.manage

import de.simplyroba.pixoobridge.config.PixooConfig
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manage")
class ManageController(private val config: PixooConfig) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/display/on/{mode}")
    fun manageDisplay(@PathVariable mode: Boolean): ResponseEntity<Unit> {

        logger.info("Wie geil ist das denn? Sehr geil!")
        logger.info("Host: ${config.host}")

        return ResponseEntity.ok().build()
    }
}