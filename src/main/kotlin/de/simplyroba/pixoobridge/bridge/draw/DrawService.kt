package de.simplyroba.pixoobridge.bridge.draw

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.format.FormatDetector
import de.simplyroba.pixoobridge.bridge.draw.model.RGB
import de.simplyroba.pixoobridge.client.PixooClient
import de.simplyroba.pixoobridge.config.PixooConfig
import java.awt.Color
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class DrawService(private val pixooConfig: PixooConfig, private val pixooClient: PixooClient) {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun drawColor(rgb: RGB) {
    val size = pixooConfig.size
    val image = ImmutableImage.filled(size, size, Color(rgb.red, rgb.green, rgb.blue))
    sendImage(image)
  }

  fun drawImage(resource: Resource) {
    val size = pixooConfig.size
    val format = detectFormat(resource)
    val resizedImage =
      resource.inputStream
        .use { inputStream -> ImmutableImage.loader().fromStream(inputStream) }
        .cover(size, size)
    sendImage(resizedImage)
  }

  private fun detectFormat(resource: Resource) {
    // its important to get a fresh unused input stream here and also not to reuse the input
    // stream from here, as the FormatDetector does not reset the stream before or after usage.
    resource.inputStream.use { inputStream -> FormatDetector.detect(inputStream) }
  }

  private fun sendImage(image: ImmutableImage) {
    val id = getNextId()
    pixooClient.sendAnimation(1, pixooConfig.size, 0, id, 1000, image.toBase64())
  }

  private fun getNextId() =
    pixooClient.getNextPictureId().parameters["PicId"].toString().toIntOrNull() ?: 1

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
