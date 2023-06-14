package de.simplyroba.pixoobridge.client

enum class CommandType(val value: String) {
    SCREEN_SWITCH("Channel/OnOffScreen");

    override fun toString(): String {
        return "$name ($value)"
    }
}