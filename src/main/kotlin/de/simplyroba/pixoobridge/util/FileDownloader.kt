package de.simplyroba.pixoobridge.util

import de.simplyroba.pixoobridge.config.PixooConfig
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class FileDownloader(private val pixooConfig: PixooConfig) {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun download(link: String): Resource {

    val webClient =
      WebClient.builder()
        .codecs { configurer ->
          configurer
            .defaultCodecs()
            .maxInMemorySize(pixooConfig.bridge.maxImageSize.toBytes().toInt())
        }
        .build()

    val bytes =
      webClient
        .get()
        .uri(link)
        .retrieve()
        .bodyToMono(ByteArray::class.java)
        .doOnError { e -> logger.warn("Error while retrieving file", e) } // log the error
        // will return an empty byte array on http error like 404
        .onErrorResume { _ -> Mono.empty() }
        .block()

    if (bytes != null && bytes.isNotEmpty()) return ByteArrayResource(bytes)
    else throw RemoteFileNotFoundException()
  }
}
