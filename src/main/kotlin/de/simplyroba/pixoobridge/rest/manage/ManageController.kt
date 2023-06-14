package de.simplyroba.pixoobridge.rest.manage

import de.simplyroba.pixoobridge.client.PixooDeviceClient
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manage")
class ManageController(private val pixooClient: PixooDeviceClient) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/display/on/{bool}")
    fun manageDisplay(@PathVariable("bool") on: Boolean): ResponseEntity<Unit> {
        pixooClient.switchDisplay(on)
        return ResponseEntity.ok().build()
    }
}