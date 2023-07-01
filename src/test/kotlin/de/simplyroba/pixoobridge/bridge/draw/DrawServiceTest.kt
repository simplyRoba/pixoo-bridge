package de.simplyroba.pixoobridge.bridge.draw

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import java.awt.Color
import java.util.Base64
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DrawServiceTest {

  @Test
  fun test() {
    val pixels: Array<Pixel> = ImmutableImage.create(64, 64).fill(Color(255, 255, 255)).pixels()

    val test =
      pixels
        .map { pixel -> pixel.toRGB() }
        .flatMap { intArray -> intArray.asSequence() }
        .map { int -> int.toByte() }
        .toByteArray()

    val encodeToString = Base64.getEncoder().withoutPadding().encodeToString(test)
    println(encodeToString)
  }
}
