package com.shilapi.xcertplay

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Lynk820AProfileTest {
    @Test fun matchesTheOriginalPhotographedDisplayString() {
        assertTrue(Lynk820AProfile.matches(
            "dcy11_a1-user 9 PQ3B.190801.002 eng.vc.int.20230824.095046 test-keys",
            28,
        ))
    }

    @Test fun matchesTheFieldsExportedByTheActual820ALog() {
        assertTrue(Lynk820AProfile.matches(
            display = "PQ3B.190801.002 test-keys",
            sdkInt = 28,
            hardware = "dcy11_a1",
            buildId = "PQ3B.190801.002",
            fingerprint = "geely/dcy11_a1/dcy11:9/PQ3B.190801.002/vc.integrator08240950:user/test-keys",
        ))
    }

    @Test fun doesNotChangeOtherHeadUnits() {
        assertFalse(Lynk820AProfile.matches(
            display = "PQ3B.190801.002 test-keys",
            sdkInt = 29,
            hardware = "dcy11_a1",
            buildId = "PQ3B.190801.002",
        ))
        assertFalse(Lynk820AProfile.matches(
            display = "PQ3B.190801.002 test-keys",
            sdkInt = 28,
            hardware = "qcom",
            buildId = "PQ3B.190801.002",
        ))
    }

}
