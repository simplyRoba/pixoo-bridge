package de.simplyroba.pixoobridge.bridge.draw.model

//TODO Schema
data class TextRequest(
  val id: Int,
  val position: Position,
  val scrollDirection: ScrollDirection,
  val font: Int,
  val textWidth: Int,
  val text: String,
  val scrollSpeed: Int,
  val color: RGB,
  val textAlignment: TextAlignment
)
