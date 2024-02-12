package de.simplyroba.pixoobridge.util

import de.simplyroba.pixoobridge.config.PixooConfig
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class FileDownloader(private val pixooConfig: PixooConfig) {
  fun download(link: String): Resource {

    val webClient =
      WebClient.builder()
        .codecs { configurer ->
          configurer.defaultCodecs().maxInMemorySize(pixooConfig.maxImageSize.toBytes().toInt())
        }
        .build()

    val bytes =
      webClient
        .get()
        .uri(link)
        .retrieve()
        .bodyToMono(ByteArray::class.java)
        .onErrorComplete() // will return an empty byte array on http error like 404
        .block()

    if (bytes != null && bytes.isNotEmpty()) return ByteArrayResource(bytes)
    else throw RemoteFileNotFoundException()
  }
}
