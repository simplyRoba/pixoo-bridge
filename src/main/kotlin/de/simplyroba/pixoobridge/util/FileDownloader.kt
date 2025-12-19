package de.simplyroba.pixoobridge.util

import de.simplyroba.pixoobridge.config.PixooConfig
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class FileDownloader(private val pixooConfig: PixooConfig) {

  private val logger = LoggerFactory.getLogger(javaClass)
  private val restClient = RestClient.builder().build()

  fun download(link: String): Resource {
    logger.debug("Starting download from link: {}", link)

    return try {
      restClient.get().uri(link).exchange { _, response ->
        // 1. Check if the request was successful
        if (!response.statusCode.is2xxSuccessful) {
          logger.warn("File not found or server error: {}", response.statusCode)
          throw RemoteFileNotFoundException("Server returned ${response.statusCode}")
        }

        // 2. Check the Content-Length header BEFORE consuming the body
        val contentLength = response.headers.contentLength
        val maxSize = pixooConfig.bridge.maxImageSize.toBytes()

        if (contentLength > maxSize) {
          logger.error("File is too large: {} bytes (max: {})", contentLength, maxSize)
          throw IllegalArgumentException("File exceeds maximum allowed size")
        }

        // 3. If size is okay, read the body
        val bytes = response.body.readAllBytes()
        if (bytes.isEmpty()) {
          logger.warn("Downloaded file is empty from link: {}", link)
          throw RemoteFileNotFoundException("Received empty body from $link")
        }

        logger.debug("Download successful, received {} bytes", bytes.size)
        ByteArrayResource(bytes)
      }
    } catch (ex: Exception) {
      // Wrap all exceptions into RemoteFileNotFoundException.
      // As the caller only wants an Image. If it fails, there is no image.
      throw when (ex) {
        is RemoteFileNotFoundException -> ex
        else -> RemoteFileNotFoundException("Download failed for $link", ex)
      }
    }
  }
}
