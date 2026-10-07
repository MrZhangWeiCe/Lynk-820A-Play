package com.shilapi.xcertplay

import android.content.Context
import android.os.Build
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class Lynk820AAudioFocusTest {
    private val context get() = RuntimeEnvironment.getApplication()

    @Before fun clearPreferences() {
        context.getSharedPreferences("xcertplay_airplay", Context.MODE_PRIVATE)
            .edit().clear().apply()
    }

    @Test fun rendererDefaultsToSourceActivationOn820A() {
        use820A()
        assertTrue(AirPlayPersistence.loadAudioFocusEnabled(context))
    }

    @Test fun explicitOptOutAndOptInAreRetained() {
        use820A()
        AirPlayPersistence.saveAudioFocusEnabled(context, false)
        assertFalse(AirPlayPersistence.loadAudioFocusEnabled(context))
        AirPlayPersistence.saveAudioFocusEnabled(context, true)
        assertTrue(AirPlayPersistence.loadAudioFocusEnabled(context))
    }

    @Test fun unrelatedHeadUnitKeepsPreviousDefault() {
        ReflectionHelpers.setStaticField(Build::class.java, "DISPLAY", "other")
        ReflectionHelpers.setStaticField(Build::class.java, "HARDWARE", "other")
        assertFalse(AirPlayPersistence.loadAudioFocusEnabled(context))
    }

    private fun use820A() {
        ReflectionHelpers.setStaticField(Build::class.java, "DISPLAY",
            "dcy11_a1-user 9 PQ3B.190801.002 test-keys")
    }
}
