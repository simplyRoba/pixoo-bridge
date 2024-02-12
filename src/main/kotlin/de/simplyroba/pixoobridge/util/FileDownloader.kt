package de.simplyroba.pixoobridge.util

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class FileDownloader {
  fun download(link: String): Resource {
    val bytes =
      WebClient.create().get().uri(link).retrieve().bodyToMono(ByteArray::class.java)
        .onErrorComplete() // will return an empty byte array on http error like 404
        .block()

    if (bytes != null && bytes.isNotEmpty()) return ByteArrayResource(bytes)
    else throw RemoteFileNotFoundException()
  }
}
