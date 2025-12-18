package de.simplyroba.pixoobridge.bridge.sound.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Sound")
data class SoundRequest(
  // Times are in ms. There are no official limits documented, these are just reasonable
  // assumptions. e.g. the active cycle should be at least 100ms to be audible. Sound playing longer
  // than 100s seems unreasonable.
  @param:Schema(minimum = "100", maximum = "10000") val activeTimeInCycle: Int,
  @param:Schema(minimum = "0", maximum = "10000") val offTimeInCycle: Int,
  @param:Schema(minimum = "100", maximum = "100000") val totalPlayTime: Int,
) {
  fun valid() =
    activeTimeInCycle in 100..10000 && offTimeInCycle in 0..10000 && totalPlayTime in 100..100000
}
