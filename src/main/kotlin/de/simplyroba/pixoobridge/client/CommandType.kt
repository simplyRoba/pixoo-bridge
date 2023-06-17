package de.simplyroba.pixoobridge.client

import com.fasterxml.jackson.annotation.JsonValue

enum class CommandType(@get:JsonValue val value: String) {
    ON_OFF_SCREEN("Channel/OnOffScreen"),
    SET_BRIGHTNESS("Channel/SetBrightness"),
    GET_CONFIGURATION("Channel/GetAllConf"),
    SET_SYSTEM_TIME("Device/SetUTC"),
    SET_SYSTEM_TIME_MODE("Device/SetTime24Flag"),
    SET_SYSTEM_TIME_ZONE("Sys/TimeZone"),
    GET_SYSTEM_TIME("Device/GetDeviceTime");

    override fun toString(): String {
        return "$name ($value)"
    }
}