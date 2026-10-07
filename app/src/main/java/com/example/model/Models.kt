package com.example.model

enum class EmergencyStatus {
    SAFE,
    VERIFYING,
    ESCALATING,
    DISPATCHED,
    RESOLVED
}

enum class SeverityLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class EmergencyType(val label: String) {
    FALL_DETECTED("Possible Fall"),
    PROLONGED_INACTIVITY("Prolonged Inactivity"),
    VOICE_DISTRESS("Voice Triggered SOS"),
    VEHICLE_COLLISION("Abnormal Motion / Collision"),
    MANUAL_SOS("Manual SOS"),
    UNKNOWN("Distress Event")
}

data class TimelineEvent(
    val id: String,
    val timeStr: String,
    val title: String,
    val description: String,
    val category: String, // DETECTION, REASONING, VERIFICATION, ESCALATION, NOTIFICATION, RESOLUTION
    val isCompleted: Boolean = true
)

data class AIAssessment(
    val situation: String,
    val confidencePercentage: Int,
    val severity: SeverityLevel,
    val userResponseStatus: String,
    val recommendedAction: String,
    val reasoningPoints: List<String>,
    val summary: String,
    val modelEngine: String = "Nexora Multi-Signal Sensor AI (Edge + Azure AI)"
)

data class EmergencyContact(
    val id: String,
    val name: String,
    val relationship: String,
    val phone: String,
    val isPrimary: Boolean = false,
    val isNotified: Boolean = false,
    val lastContactedTime: String? = null
)

data class MedicalProfile(
    val fullName: String = "Alex Chen",
    val age: Int = 26,
    val bloodType: String = "O+",
    val allergies: String = "Penicillin, Tree Nuts",
    val medications: String = "Albuterol Inhaler (PRN)",
    val emergencyNotes: String = "Subject has mild asthma. Emergency contacts authorized to make medical decisions.",
    val organDonor: Boolean = true
)

data class SensorTelemetry(
    val accelX: Float = 0.04f,
    val accelY: Float = 0.12f,
    val accelZ: Float = 9.81f,
    val totalG: Float = 1.0f,
    val gyroPitch: Float = 1.2f,
    val gyroRoll: Float = 0.5f,
    val inactivitySeconds: Int = 0,
    val heartRateBpm: Int = 74,
    val ambientDecibels: Float = 42.0f,
    val anomalyScore: Float = 0.05f
)

data class FacilityPin(
    val id: String,
    val name: String,
    val type: String, // HOSPITAL, POLICE, FIRE_STATION
    val distanceKm: Float,
    val etaMinutes: Int,
    val phone: String,
    val xNorm: Float, // 0.0 .. 1.0 on visual map
    val yNorm: Float
)

data class Incident(
    val id: String,
    val subjectName: String,
    val type: EmergencyType,
    val title: String,
    val status: EmergencyStatus,
    val severity: SeverityLevel,
    val confidence: Int,
    val detectedTime: String,
    val locationName: String,
    val coordinates: String,
    val lastResponse: String,
    val currentAction: String,
    val aiAssessment: AIAssessment,
    val timeline: List<TimelineEvent>,
    val telemetry: SensorTelemetry,
    val isDemoSimulation: Boolean = true,
    val resolvedAt: String? = null
)

data class AnalyticsData(
    val incidentsDetected: Int = 128,
    val incidentsResolved: Int = 122,
    val avgResponseSeconds: Int = 18,
    val falseAlarmRatePercent: Int = 7,
    val successfulVerificationPercent: Int = 91,
    val fallCount: Int = 54,
    val inactivityCount: Int = 31,
    val voiceCount: Int = 23,
    val vehicleCount: Int = 20
)

data class AccessibilityConfig(
    val highContrast: Boolean = false,
    val largeButtons: Boolean = false,
    val minimalText: Boolean = false,
    val voiceFeedback: Boolean = true,
    val hapticsEnabled: Boolean = true
)
