package de.simplyroba.pixoobridge.client

import com.fasterxml.jackson.annotation.JsonValue

enum class CommandType(@get:JsonValue val value: String) {
    ON_OFF_SCREEN("Channel/OnOffScreen"),
    SET_BRIGHTNESS("Channel/SetBrightness"),
    GET_CONFIGURATION("Channel/GetAllConf");

    override fun toString(): String {
        return "$name ($value)"
    }
}