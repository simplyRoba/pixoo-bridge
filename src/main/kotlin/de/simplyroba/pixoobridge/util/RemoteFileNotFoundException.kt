package de.simplyroba.pixoobridge.util

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Could not download remote file")
class RemoteFileNotFoundException(message: String? = null, cause: Throwable? = null) :
  RuntimeException(message, cause)
