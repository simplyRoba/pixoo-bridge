package de.simplyroba.pixoobridge.rest.draw

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/draw")
class DrawController {

    @PostMapping("/fill")
    fun fill() {
        throw RuntimeException("Not yet implemented!")
    }
}