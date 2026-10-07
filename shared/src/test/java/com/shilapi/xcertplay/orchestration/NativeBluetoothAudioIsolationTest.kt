package com.shilapi.xcertplay.orchestration

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Intent
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

interface DisconnectableTestProfile : BluetoothProfile {
    fun disconnect(device: BluetoothDevice): Boolean
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class NativeBluetoothAudioIsolationTest {
    private val context = RuntimeEnvironment.getApplication()
    private val adapter = mock(BluetoothAdapter::class.java)
    private val target = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("00:11:22:33:44:55")
    private val messages = mutableListOf<String>()
    private val guard = NativeBluetoothAudioIsolation(context, adapter, target, messages::add)

    private fun listeners(): List<BluetoothProfile.ServiceListener> {
        guard.start()
        val capture = ArgumentCaptor.forClass(BluetoothProfile.ServiceListener::class.java)
        verify(adapter, times(2)).getProfileProxy(eq(context), capture.capture(), anyInt())
        return capture.allValues
    }

    private fun profile(): DisconnectableTestProfile = mock(DisconnectableTestProfile::class.java).also {
        `when`(it.getConnectionState(target)).thenReturn(BluetoothProfile.STATE_CONNECTED)
        `when`(it.disconnect(target)).thenReturn(true)
    }

    @Test fun disconnectsOnlySelectedDeviceAndOnlySinkProfiles() {
        val callbacks = listeners()
        val sink = profile()
        callbacks[0].onServiceConnected(11, sink)
        verify(sink).disconnect(target)
        verify(adapter).getProfileProxy(eq(context), any(BluetoothProfile.ServiceListener::class.java), eq(11))
        verify(adapter).getProfileProxy(eq(context), any(BluetoothProfile.ServiceListener::class.java), eq(16))
        val other = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("00:11:22:33:44:66")
        broadcast(other)
        verify(sink, times(1)).disconnect(target)
        guard.close()
        verify(adapter).closeProfileProxy(11, sink)
    }

    @Test fun stopsAfterThreeAttemptsAndStopsOnSessionClose() {
        val callback = listeners()[0]
        val sink = profile()
        callback.onServiceConnected(11, sink)
        repeat(4) { broadcast(target) }
        verify(sink, times(3)).disconnect(target)
        assertTrue(messages.any { it.contains("reconnectLimitReached=true") })
        guard.close()
        broadcast(target)
        verify(sink, times(3)).disconnect(target)
    }

    @Test fun delayedServiceCallbackCannotDisconnectAfterClose() {
        val callback = listeners()[0]
        guard.close()
        val sink = profile()
        callback.onServiceConnected(11, sink)
        verify(sink, never()).disconnect(target)
        verify(adapter).closeProfileProxy(11, sink)
    }

    @Test fun disconnectedProfileIsLeftAlone() {
        val callback = listeners()[0]
        val sink = profile()
        `when`(sink.getConnectionState(target)).thenReturn(BluetoothProfile.STATE_DISCONNECTED)
        callback.onServiceConnected(11, sink)
        verify(sink, never()).disconnect(target)
        guard.close()
    }

    private fun broadcast(device: BluetoothDevice) {
        context.sendBroadcast(Intent("android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED")
            .putExtra(BluetoothDevice.EXTRA_DEVICE, device)
            .putExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_CONNECTED))
        shadowOf(android.os.Looper.getMainLooper()).idle()
    }
}
