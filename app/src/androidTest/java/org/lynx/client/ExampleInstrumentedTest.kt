package org.lynx.client

import android.util.Base64
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("org.peerstock.client", appContext.packageName)
    }

    @Test
    fun emptyBase64() {
        assertEquals("", Base64.encodeToString(ByteArray(0), Base64.NO_WRAP))
    }

    @Test
    fun emptyBase64Decode() {
        assertArrayEquals(ByteArray(0), Base64.decode("", Base64.NO_WRAP))
    }
}