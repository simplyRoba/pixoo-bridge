package de.simplyroba.pixoobridge.client

enum class CommandType(val value: String) {
    ON_OFF_SCREEN("Channel/OnOffScreen");

    override fun toString(): String {
        return "$name ($value)"
    }
}