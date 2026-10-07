package com.shilapi.xcertplay

import org.junit.Assert.assertEquals
import org.junit.Test

class LynkPlayIdentityTest {
    @Test
    fun carPlayReceiverAdvertisesLynkPlay() {
        assertEquals("LynkPlay", DiPlayBootstrap.CARPLAY_DEVICE_NAME)
    }

    @Test
    fun carPlayCarButtonUsesLynkLabel() {
        assertEquals("Lynk", AirPlayPersistence.DEFAULT_OEM_LABEL)
    }
}
