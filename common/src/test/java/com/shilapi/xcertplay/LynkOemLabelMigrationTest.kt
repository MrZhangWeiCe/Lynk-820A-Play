package com.shilapi.xcertplay

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class LynkOemLabelMigrationTest {
    private val context: Context get() = RuntimeEnvironment.getApplication()

    @Test fun migratesPackagedBydAndIntermediateLynkPlayLabels() {
        AirPlayPersistence.saveOemLabel(context, "BYD")
        assertEquals("Lynk", AirPlayPersistence.loadOemLabel(context))

        AirPlayPersistence.saveOemLabel(context, "LynkPlay")
        assertEquals("Lynk", AirPlayPersistence.loadOemLabel(context))
    }

    @Test fun preservesAUserEnteredCarButtonLabel() {
        AirPlayPersistence.saveOemLabel(context, "My 01")
        assertEquals("My 01", AirPlayPersistence.loadOemLabel(context))
    }
}
