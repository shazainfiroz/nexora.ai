package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.AiEmergencyEngine
import com.example.model.EmergencyType
import com.example.model.SensorTelemetry
import com.example.model.SeverityLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Nexora Guardian", appName)
    }

    @Test
    fun `ai emergency engine calculates calibrated fall assessment`() {
        val telemetry = SensorTelemetry(
            accelX = 1.2f, accelY = 2.4f, accelZ = 3.8f, totalG = 4.6f,
            gyroPitch = 45f, inactivitySeconds = 6
        )
        val assessment = AiEmergencyEngine.evaluateSignals(
            type = EmergencyType.FALL_DETECTED,
            telemetry = telemetry,
            userResponse = "No response"
        )

        assertEquals("Possible High-Impact Fall", assessment.situation)
        assertEquals(92, assessment.confidencePercentage)
        assertEquals(SeverityLevel.HIGH, assessment.severity)
        assertTrue(assessment.reasoningPoints.isNotEmpty())
        assertTrue(assessment.summary.contains("92% AI confidence"))
    }
}
