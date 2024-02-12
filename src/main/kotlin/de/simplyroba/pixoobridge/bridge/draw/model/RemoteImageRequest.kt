package de.simplyroba.pixoobridge.bridge.draw.model

import io.swagger.v3.oas.annotations.media.Schema
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException

@Schema(name = "Remote image")
data class RemoteImageRequest(val link: String)

fun RemoteImageRequest.valid(): Boolean {
  try {
    URI(link).toURL()
  } catch (ex: Exception) {
    when (ex) {
      is MalformedURLException,
      is URISyntaxException -> return false
      else -> throw ex
    }
  }
  return true
}
