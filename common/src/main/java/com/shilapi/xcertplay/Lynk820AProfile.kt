package com.shilapi.xcertplay

import android.os.Build

/** Verified Android 9 profile from the Lynk & Co 01 global 820A head unit. */
internal object Lynk820AProfile {
    private const val LEGACY_DISPLAY_PREFIX = "dcy11_a1-user 9 PQ3B.190801.002"
    private const val HARDWARE = "dcy11_a1"
    private const val BUILD_ID = "PQ3B.190801.002"
    private const val FINGERPRINT_PREFIX = "geely/dcy11_a1/dcy11:9/PQ3B.190801.002/"

    fun matches(
        display: String? = Build.DISPLAY,
        sdkInt: Int = Build.VERSION.SDK_INT,
        hardware: String? = Build.HARDWARE,
        buildId: String? = Build.ID,
        fingerprint: String? = Build.FINGERPRINT,
    ): Boolean = sdkInt == Build.VERSION_CODES.P && (
        display.orEmpty().startsWith(LEGACY_DISPLAY_PREFIX, ignoreCase = true) ||
            hardware.orEmpty().equals(HARDWARE, ignoreCase = true) && (
                buildId.orEmpty().equals(BUILD_ID, ignoreCase = true) ||
                    fingerprint.orEmpty().startsWith(FINGERPRINT_PREFIX, ignoreCase = true)
                )
        )

}
