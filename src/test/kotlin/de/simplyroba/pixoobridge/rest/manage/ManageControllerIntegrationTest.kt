package de.simplyroba.pixoobridge.rest.manage

import de.simplyroba.pixoobridge.AbstractIntegrationTest
import org.junit.jupiter.api.Test

class ManageControllerIntegrationTest: AbstractIntegrationTest() {

    @Test
    fun `should turn display on`() {
        callBridge("/manage/display/on")
        verifyCommand("{\"Command\":\"Channel/OnOffScreen\", \"OnOff\": 1}")
    }

    @Test
    fun `should turn display off`() {
        callBridge("/manage/display/off")
        verifyCommand("{\"Command\":\"Channel/OnOffScreen\", \"OnOff\": 0}")
    }

    @Test
    fun `should set brightness`() {
        callBridge("/manage/display/brightness/65")
        verifyCommand("{\"Command\":\"Channel/SetBrightness\", \"Brightness\": 65}")
    }
}