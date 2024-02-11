package de.simplyroba.pixoobridge.bridge.draw

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ImageDownloader {
  fun download(link: String): Resource {
    val bytes =
      WebClient.create().get().uri(link).retrieve().bodyToMono(ByteArray::class.java).block()

    // TODO handle null
    return bytes?.let { ByteArrayResource(it) }!!
  }
}
