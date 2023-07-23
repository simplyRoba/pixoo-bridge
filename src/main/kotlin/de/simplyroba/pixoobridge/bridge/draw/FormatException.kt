package de.simplyroba.pixoobridge.bridge.draw

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Unsupported format")
class FormatException(override val message: String) : RuntimeException(message)
