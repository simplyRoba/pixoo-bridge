package de.simplyroba.pixoobridge.bridge.draw

import com.sksamuel.scrimage.ImmutableImage
import de.simplyroba.pixoobridge.bridge.draw.model.RGB
import de.simplyroba.pixoobridge.client.PixooClient
import de.simplyroba.pixoobridge.config.PixooConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.awt.Color

@Service
class DrawService(
  private val pixooConfig: PixooConfig,
  private val pixooClient: PixooClient
) {

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
    pixooClient.sendAnimation()
  }
}
