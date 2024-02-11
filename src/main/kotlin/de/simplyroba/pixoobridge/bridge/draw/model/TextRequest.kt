package de.simplyroba.pixoobridge.bridge.draw.model

import io.swagger.v3.oas.annotations.media.Schema

data class TextRequest(
  @Schema(minimum = "0", maximum = "20") val id: Int,
  val position: Position,
  val scrollDirection: ScrollDirection,
  @Schema(minimum = "0", maximum = "7") val font: Int,
  @Schema(minimum = "16", maximum = "64") val textWidth: Int,
  @Schema(maxLength = 512) val text: String,
  @Schema(minimum = "0", maximum = "100") val scrollSpeed: Int,
  val color: RgbColor,
  val textAlignment: TextAlignment
)

fun TextRequest.valid() =
  id in 0..20 &&
    position.valid() &&
    font in 0..7 &&
    textWidth in 16..64 &&
    text.length <= 512 &&
    scrollSpeed in 0..100 &&
    color.valid()
