package de.simplyroba.pixoobridge.client

import com.fasterxml.jackson.annotation.JsonValue

enum class CommandType(@get:JsonValue val value: String) {
  // Manage
  SET_DISPLAY_ON_OFF("Channel/OnOffScreen"),
  SET_DISPLAY_BRIGHTNESS("Channel/SetBrightness"),
  SET_DISPLAY_BRIGHTNESS_OVERCLOCK("Device/SetHighLightMode"),
  SET_DISPLAY_ROTATION("Device/SetScreenRotationAngle"),
  SET_DISPLAY_MIRRORED("Device/SetMirrorMode"),
  SET_DISPLAY_WHITE_BALANCE("Device/SetWhiteBalance"),
  SET_SYSTEM_TIME("Device/SetUTC"),
  SET_SYSTEM_TIME_MODE("Device/SetTime24Flag"),
  SET_SYSTEM_TIME_ZONE("Sys/TimeZone"),
  GET_SYSTEM_TIME("Device/GetDeviceTime"),
  SET_WEATHER_LOCATION("Sys/LogAndLat"),
  SET_WEATHER_TEMP_UNIT("Device/SetDisTempMode"),
  GET_WEATHER_INFO("Device/GetWeatherInfo"),
  GET_CONFIGURATION("Channel/GetAllConf"),
  // Channel
  SET_CHANNEL_GALLERY_TIME("Channel/SetSubscribeGalleryTime");

  override fun toString(): String {
    return "$name ($value)"
  }
}
