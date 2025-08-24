package de.simplyroba.pixoobridge.util

import de.simplyroba.pixoobridge.client.PixooClient.Companion.DEFAULT_TIMEOUT
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class FileDownloader() {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun download(link: String): Resource {

    val restClient =
      RestClient.builder()
        .requestFactory(
          HttpComponentsClientHttpRequestFactory().apply {
            setConnectTimeout(DEFAULT_TIMEOUT.inWholeMilliseconds.toInt())
            setReadTimeout(DEFAULT_TIMEOUT.inWholeMilliseconds.toInt())
          }
        )
        .build()

    val bytes =
      restClient
        .get()
        .uri(link)
        .retrieve()
        .onStatus(
          { it.is4xxClientError || it.is5xxServerError },
          { _, response ->
            logger.error(
              "Error while retrieving file: ${response.statusCode} - ${response.statusText}"
            )
            throw RemoteFileNotFoundException()
          },
        )
        .body(ByteArray::class.java)

    if (bytes != null && bytes.isNotEmpty()) return ByteArrayResource(bytes)
    else {
      logger.error("Downloaded file was empty")
      throw RemoteFileNotFoundException()
    }
  }
}
