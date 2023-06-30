package de.simplyroba.pixoobridge.bridge.draw

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.awt.Color
import java.util.Base64
import kotlin.streams.asStream

class DrawServiceTest {

  @Test
  fun test() {
    val pixels: Array<Pixel> = ImmutableImage.create(64, 64).fill(Color(128, 128, 128)).pixels()

    val test = pixels.map { it.toRGB() }
            .flatMap { it.asSequence() }
            .map { it.toByte() }
            .toByteArray()

    val encodeToString = Base64.getEncoder().withoutPadding().encodeToString(test)
    println(encodeToString)
  }
}