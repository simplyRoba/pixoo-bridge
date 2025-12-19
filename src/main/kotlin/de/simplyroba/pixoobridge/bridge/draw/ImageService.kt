package de.simplyroba.pixoobridge.bridge.draw

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.format.Format
import com.sksamuel.scrimage.format.Format.*
import com.sksamuel.scrimage.format.FormatDetector
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import de.simplyroba.pixoobridge.bridge.draw.model.RgbColor
import de.simplyroba.pixoobridge.client.PixooClient
import de.simplyroba.pixoobridge.config.PixooConfig
import de.simplyroba.pixoobridge.util.FileDownloader
import java.awt.Color
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class ImageService(
  private val pixooConfig: PixooConfig,
  private val pixooClient: PixooClient,
  private val imageDownloader: FileDownloader,
) {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun drawColor(rgbColor: RgbColor) {
    val size = pixooConfig.size
    val image =
      ImmutableImage.filled(size, size, Color(rgbColor.red, rgbColor.green, rgbColor.blue))
    val id = getNextId()
    pixooClient.sendAnimation(1, pixooConfig.size, 0, id, 9999, image.toBase64())
  }

  fun drawRemoteImage(link: String) {
    drawImage(imageDownloader.download(link))
  }

  fun drawImage(resource: Resource) {
    logger.debug("Preparing to draw image from resource: {}", resource.readableName())
    // also checks if its valid image
    val format = detectFormat(resource)

    resource.inputStream.use { inputStream ->
      val imageSource = ImageSource.of(inputStream)

      when (format) {
        JPEG,
        PNG -> {
          logger.debug("Sending single image for file: {}", resource.readableName())
          sendSingleImage(imageSource)
        }
        // of course its possible that a gif contains only one frame this will result in the same
        // command to the pixoo as in sendSingleImage()
        GIF -> {
          logger.debug("Sending animation for GIF file: {}", resource.readableName())
          sendAnimation(imageSource)
        }
        WEBP -> throw FormatException("Unsupported format webp detected.")
      }
    }
  }

  private fun sendSingleImage(imageSource: ImageSource) {
    val size = pixooConfig.size
    logger.debug("Resizing and sending single image with target size {} x {}", size, size)
    val resizedImage =
      ImmutableImage.loader()
        .detectMetadata(false)
        .detectOrientation(false)
        .load(imageSource)
        .cover(size, size)
    // we send an animation with 1 frame
    pixooClient.sendAnimation(1, size, 0, getNextId(), 9999, resizedImage.toBase64())
  }

  private fun sendAnimation(imageSource: ImageSource) {
    val size = pixooConfig.size
    val gif = AnimatedGifReader.read(imageSource)
    val id = getNextId()
    logger.debug(
      "Resizing and sending GIF animation with {} frames, PicId {}, target size {} x {}",
      gif.frameCount,
      id,
      size,
      size,
    )
    gif.frames.forEachIndexed { index, frame ->
      val resizedFrame = frame.cover(size, size)
      val animationSpeed =
        (gif.getDelay(index).toMillis() * pixooConfig.animationSpeedFactor).toInt()
      logger.debug(
        "Sending frame {}/{} with delay {} ms",
        index + 1,
        gif.frameCount,
        animationSpeed,
      )
      pixooClient.sendAnimation(
        gif.frameCount,
        size,
        index,
        id,
        animationSpeed,
        resizedFrame.toBase64(),
      )
      if (index == 59) {
        logger.warn("Stop on 59th frame of {} frames.", gif.frameCount)
        return
      }
    }
  }

  private fun detectFormat(resource: Resource): Format {
    // it's important to get a fresh unused input stream here and also not to reuse the input
    // stream from here, as the FormatDetector does not reset the stream before or after usage.
    val format =
      resource.inputStream
        .use { inputStream -> FormatDetector.detect(inputStream) }
        .orElseThrow { FormatException("Unknown image format for ${resource.readableName()}") }
    logger.debug("Format detection: file {} is {}", resource.readableName(), format)
    return format
  }

  private fun getNextId() =
    pixooClient.getNextPictureId().parameters["PicId"].toString().toIntOrNull()?.also {
      logger.debug("Fetched next PicId: {}", it)
    } ?: 1

  private fun ImmutableImage.toBase64(): String {
    val pixelByteArray =
      this.pixels() // get pixels
        .map { pixel -> pixel.toRGB() } // map pixels to rgb values
        .flatMap { rgbIntArray -> rgbIntArray.asSequence() } // concat all rgb values of all pixels
        .map { colorValue -> colorValue.toByte() } // map all values to byte
        .toByteArray()

    return Base64.getEncoder().withoutPadding().encodeToString(pixelByteArray)
  }
}

/**
 * Returns a human-readable identifier for this [Resource], preferring the file name when available
 * and falling back to the resource description otherwise.
 */
fun Resource.readableName(): String {
  return this.filename ?: this.description
}
