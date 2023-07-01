package de.simplyroba.pixoobridge.bridge.draw

import com.sksamuel.scrimage.ImmutableImage
import de.simplyroba.pixoobridge.bridge.draw.model.RGB
import de.simplyroba.pixoobridge.client.PixooClient
import de.simplyroba.pixoobridge.config.PixooConfig
import java.awt.Color
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DrawService(private val pixooConfig: PixooConfig, private val pixooClient: PixooClient) {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun fill(rgb: RGB) {
    val image = createImage().fill(Color(rgb.red, rgb.green, rgb.blue))
    sendImage(image)
  }

  private fun createImage(): ImmutableImage {
    val size = pixooConfig.size
    return ImmutableImage.create(size, size)
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
