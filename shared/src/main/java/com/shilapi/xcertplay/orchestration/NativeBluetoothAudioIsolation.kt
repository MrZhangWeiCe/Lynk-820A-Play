package com.shilapi.xcertplay.orchestration

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.io.Closeable

/** Session-scoped Android 9 handoff. Never disables Bluetooth or changes pairing/priority. */
internal class NativeBluetoothAudioIsolation(
    private val context: Context,
    private val adapter: BluetoothAdapter,
    private val target: BluetoothDevice,
    private val report: (String) -> Unit,
) : Closeable {
    private val proxies = mutableMapOf<Int, BluetoothProfile>()
    private val attempts = mutableMapOf<Int, Int>()
    private var closed = false
    private var registered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val event = intent ?: return
            val profile = ACTIONS.entries.firstOrNull { it.value == event.action }?.key ?: return
            @Suppress("DEPRECATION")
            val device = event.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            if (device != target) return
            synchronized(this@NativeBluetoothAudioIsolation) {
                if (closed) return
                val state = event.getIntExtra(BluetoothProfile.EXTRA_STATE, -1)
                report("Bluetooth isolation profile=$profile state=$state")
                if (state == BluetoothProfile.STATE_CONNECTED) proxies[profile]?.let { disconnect(profile, it) }
            }
        }
    }

    @Synchronized
    fun start() {
        if (closed || registered) return
        try {
            context.registerReceiver(receiver, IntentFilter().apply { ACTIONS.values.forEach(::addAction) })
            registered = true
        } catch (error: Exception) {
            report("Bluetooth isolation receiver unavailable=${error.javaClass.simpleName}")
            return
        }
        ACTIONS.keys.forEach { profile ->
            try {
                val accepted = adapter.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
                    override fun onServiceConnected(actualProfile: Int, proxy: BluetoothProfile) {
                        synchronized(this@NativeBluetoothAudioIsolation) {
                            if (closed || actualProfile != profile) {
                                runCatching { adapter.closeProfileProxy(actualProfile, proxy) }
                                return
                            }
                            proxies.put(profile, proxy)?.takeIf { it !== proxy }?.let {
                                runCatching { adapter.closeProfileProxy(profile, it) }
                            }
                            disconnect(profile, proxy)
                        }
                    }
                    override fun onServiceDisconnected(actualProfile: Int) {
                        synchronized(this@NativeBluetoothAudioIsolation) { proxies.remove(actualProfile) }
                    }
                }, profile)
                report("Bluetooth isolation profile=$profile proxyRequested=$accepted")
            } catch (error: Exception) {
                report("Bluetooth isolation profile=$profile unavailable=${error.javaClass.simpleName}")
            }
        }
    }

    private fun disconnect(profile: Int, proxy: BluetoothProfile) {
        try {
            val state = proxy.getConnectionState(target)
            if (state != BluetoothProfile.STATE_CONNECTED) {
                report("Bluetooth isolation profile=$profile state=$state noDisconnectNeeded=true")
                return
            }
            val count = attempts.getOrDefault(profile, 0)
            // Do not enter a disconnect/reconnect war with an OEM auto-connect service.
            if (count >= 3) {
                report("Bluetooth isolation profile=$profile reconnectLimitReached=true")
                return
            }
            attempts[profile] = count + 1
            val accepted = proxy.javaClass.getMethod("disconnect", BluetoothDevice::class.java)
                .invoke(proxy, target) == true
            report("Bluetooth isolation profile=$profile disconnectAccepted=$accepted attempt=${count + 1}")
        } catch (error: Exception) {
            val cause = error.cause ?: error
            report("Bluetooth isolation profile=$profile disconnectUnavailable=${cause.javaClass.simpleName}")
        }
    }

    @Synchronized
    override fun close() {
        if (closed) return
        closed = true
        if (registered) runCatching { context.unregisterReceiver(receiver) }
        registered = false
        proxies.forEach { (profile, proxy) -> runCatching { adapter.closeProfileProxy(profile, proxy) } }
        proxies.clear()
        // Native Bluetooth may reconnect normally after CarPlay ends; no persistent policy was changed.
    }

    private companion object {
        val ACTIONS = mapOf(
            11 to "android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED",
            16 to "android.bluetooth.headsetclient.profile.action.CONNECTION_STATE_CHANGED",
        )
    }
}
