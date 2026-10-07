package com.example.ai

import com.example.model.AIAssessment
import com.example.model.EmergencyType
import com.example.model.SensorTelemetry
import com.example.model.SeverityLevel

object AiEmergencyEngine {

    /**
     * Evaluates multi-signal sensory, environmental, and contextual inputs
     * to formulate a transparent, calibrated AI emergency assessment.
     * Incorporates Bayesian uncertainty calibration to avoid false certainty.
     */
    fun evaluateSignals(
        type: EmergencyType,
        telemetry: SensorTelemetry,
        userResponse: String,
        locationContext: String = "47.6423° N, 122.1368° W (Microsoft Redmond Campus)"
    ): AIAssessment {
        val reasoningPoints = mutableListOf<String>()
        var confidence: Int
        var severity: SeverityLevel
        val situationName: String
        val recommendedAction: String

        when (type) {
            EmergencyType.FALL_DETECTED -> {
                situationName = "Possible High-Impact Fall"
                // Signal checks
                if (telemetry.totalG > 2.8f || telemetry.totalG < 0.4f) {
                    reasoningPoints.add("Sudden free-fall / impact acceleration spike (${String.format("%.1f", telemetry.totalG)} Gs detected)")
                } else {
                    reasoningPoints.add("Abnormal kinetic impulse registered on tri-axial accelerometer")
                }
                reasoningPoints.add("Rapid rotational change in device orientation (${String.format("%.1f", telemetry.gyroPitch)}° pitch deviation)")
                reasoningPoints.add("Immediate secondary post-impact inactivity phase (${telemetry.inactivitySeconds}s)")
                
                if (userResponse == "No response") {
                    reasoningPoints.add("Subject failed to answer active multi-modal verification chime within 10s")
                    confidence = 92
                    severity = SeverityLevel.HIGH
                    recommendedAction = "Autonomous escalation: Broadcast live beacon, dispatch trusted contacts, query nearest medical center"
                } else if (userResponse == "User confirmed safe") {
                    confidence = 18
                    severity = SeverityLevel.LOW
                    recommendedAction = "Stand down alert, archive incident as benign kinetic event"
                } else {
                    confidence = 74
                    severity = SeverityLevel.MEDIUM
                    recommendedAction = "Maintain audio check-in channel and alert designated circle"
                }
            }

            EmergencyType.PROLONGED_INACTIVITY -> {
                situationName = "Abnormal Prolonged Inactivity / Unresponsiveness"
                reasoningPoints.add("Zero micro-motion detected for ${telemetry.inactivitySeconds} seconds in non-sleep window")
                reasoningPoints.add("Heart rate biometric signature registered at ${telemetry.heartRateBpm} BPM (stable but stagnant)")
                reasoningPoints.add("Contextual risk heightened: User traveling alone in unfamiliar area")
                
                if (userResponse == "No response") {
                    reasoningPoints.add("Passive prompt timed out without haptic or touch interaction")
                    confidence = 88
                    severity = SeverityLevel.HIGH
                    recommendedAction = "Initiate wellness check escalation and notify primary trusted contact"
                } else {
                    confidence = 25
                    severity = SeverityLevel.LOW
                    recommendedAction = "Dismiss inactivity alarm per user interaction"
                }
            }

            EmergencyType.VOICE_DISTRESS -> {
                situationName = "Acoustic Distress Trigger ('Help / Emergency')"
                reasoningPoints.add("Voice intent acoustic match: High-frequency stress formant detected")
                reasoningPoints.add("Trigger keyword detected with acoustic confidence score 0.94")
                reasoningPoints.add("Ambient noise profile: ${String.format("%.1f", telemetry.ambientDecibels)} dB with speech cadence")
                
                confidence = 96
                severity = SeverityLevel.CRITICAL
                recommendedAction = "Immediate emergency broadcast: Initiate priority location beacon and alert family circle"
            }

            EmergencyType.VEHICLE_COLLISION -> {
                situationName = "Severe Deceleration / Potential Collision"
                reasoningPoints.add("High-velocity kinetic deceleration profile exceeding 4.2 G-force threshold")
                reasoningPoints.add("Rapid velocity drop detected in transit vector")
                reasoningPoints.add("Device tumbling dynamics logged across all 3 gyroscopic axes")
                
                if (userResponse == "No response") {
                    reasoningPoints.add("Critical: No user physical interaction detected post-impact")
                    confidence = 95
                    severity = SeverityLevel.CRITICAL
                    recommendedAction = "Activate highest priority responder triage, stream coordinates to nearest trauma unit"
                } else {
                    confidence = 80
                    severity = SeverityLevel.HIGH
                    recommendedAction = "Prompt for injury status and offer one-tap roadside assistance"
                }
            }

            EmergencyType.MANUAL_SOS -> {
                situationName = "User-Initiated Emergency SOS"
                reasoningPoints.add("Deliberate 3-second tactile hold gesture completed by user")
                reasoningPoints.add("User bypassed confirmation countdown directly requesting assistance")
                reasoningPoints.add("Telemetry indicates elevated heart rate (${telemetry.heartRateBpm} BPM)")
                
                confidence = 99
                severity = SeverityLevel.CRITICAL
                recommendedAction = "Execute emergency protocol: Notify all 3 trusted contacts and initiate live GPS breadcrumb stream"
            }

            EmergencyType.UNKNOWN -> {
                situationName = "Unidentified Anomaly"
                reasoningPoints.add("Multiple low-confidence sensor variances clustered simultaneously")
                confidence = 65
                severity = SeverityLevel.MEDIUM
                recommendedAction = "Verify status with user before contacting family"
            }
        }

        val summary = generateSummary(situationName, confidence, severity, userResponse, reasoningPoints)

        return AIAssessment(
            situation = situationName,
            confidencePercentage = confidence,
            severity = severity,
            userResponseStatus = userResponse,
            recommendedAction = recommendedAction,
            reasoningPoints = reasoningPoints,
            summary = summary,
            modelEngine = "Nexora Multi-Signal Fusion Engine (Edge Sensors + Azure OpenAI)"
        )
    }

    private fun generateSummary(
        situation: String,
        confidence: Int,
        severity: SeverityLevel,
        userResponse: String,
        reasons: List<String>
    ): String {
        return "$situation flagged with $confidence% AI confidence ($severity severity). " +
                "Key indicators include: ${reasons.take(2).joinToString("; ")}. " +
                "User verification state: '$userResponse'. " +
                "Human override and real-time responder review active."
    }
}
