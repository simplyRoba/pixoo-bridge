package de.simplyroba.pixoobridge.bridge.draw

import de.simplyroba.pixoobridge.client.PixooClient
import org.springframework.stereotype.Service

@Service
class TextService(private val pixooClient: PixooClient) {

  fun clear() {
    pixooClient.clearText()
  }
}
