package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiEmergencyEngine
import com.example.model.AIAssessment
import com.example.model.AccessibilityConfig
import com.example.model.AnalyticsData
import com.example.model.EmergencyContact
import com.example.model.EmergencyStatus
import com.example.model.EmergencyType
import com.example.model.FacilityPin
import com.example.model.Incident
import com.example.model.MedicalProfile
import com.example.model.SensorTelemetry
import com.example.model.SeverityLevel
import com.example.model.TimelineEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NexoraUiState(
    val currentTab: Int = 0, // 0: Guardian Mobile, 1: Responder Dashboard, 2: Analytics, 3: Pitch & Docs
    val emergencyStatus: EmergencyStatus = EmergencyStatus.SAFE,
    val countdownSeconds: Int = 10,
    val activeIncident: Incident? = null,
    val selectedIncident: Incident? = null,
    val incidentHistory: List<Incident> = emptyList(),
    val contacts: List<EmergencyContact> = emptyList(),
    val medicalProfile: MedicalProfile = MedicalProfile(),
    val telemetry: SensorTelemetry = SensorTelemetry(),
    val facilities: List<FacilityPin> = emptyList(),
    val analytics: AnalyticsData = AnalyticsData(),
    val accessibility: AccessibilityConfig = AccessibilityConfig(),
    val isSosHolding: Boolean = false,
    val sosHoldProgress: Float = 0f,
    val showEmergencyOverlay: Boolean = false,
    val showAiTransparencyDialog: Boolean = false,
    val showMedicalDialog: Boolean = false,
    val showAddContactDialog: Boolean = false,
    val showDemoScenarioMenu: Boolean = false,
    val statusBanner: String? = null,
    val responderFilter: String = "ALL", // "ALL", "ACTIVE", "RESOLVED"
    val pitchSlideIndex: Int = 0
)

class NexoraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NexoraUiState())
    val uiState: StateFlow<NexoraUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null
    private var telemetryStreamJob: Job? = null

    init {
        loadInitialData()
        startTelemetrySimulation()
    }

    private fun loadInitialData() {
        val initialContacts = listOf(
            EmergencyContact("c1", "Elena Vance (Partner)", "Spouse", "+1 (425) 882-8080", isPrimary = true),
            EmergencyContact("c2", "Dr. Marcus Chen", "Physician", "+1 (206) 555-0142", isPrimary = false),
            EmergencyContact("c3", "Redmond Campus Security", "Institution", "+1 (425) 706-0000", isPrimary = false)
        )

        val facilities = listOf(
            FacilityPin("f1", "St. Jude Regional Trauma Center", "HOSPITAL", 1.2f, 4, "+1 (425) 899-1000", 0.72f, 0.28f),
            FacilityPin("f2", "Central Police Precinct", "POLICE", 0.8f, 3, "+1 (425) 556-2500", 0.28f, 0.35f),
            FacilityPin("f3", "Redmond Fire & Rescue Station 11", "FIRE_STATION", 1.5f, 5, "+1 (425) 556-2200", 0.82f, 0.75f)
        )

        val sampleResolved = createSamplePastIncident()

        _uiState.update {
            it.copy(
                contacts = initialContacts,
                facilities = facilities,
                incidentHistory = listOf(sampleResolved),
                selectedIncident = sampleResolved
            )
        }
    }

    private fun startTelemetrySimulation() {
        telemetryStreamJob?.cancel()
        telemetryStreamJob = viewModelScope.launch {
            while (true) {
                delay(1200)
                if (_uiState.value.emergencyStatus == EmergencyStatus.SAFE) {
                    val jitterX = ((Math.random() - 0.5) * 0.08).toFloat()
                    val jitterY = ((Math.random() - 0.5) * 0.08).toFloat()
                    val jitterZ = (9.81f + (Math.random() - 0.5) * 0.12).toFloat()
                    val hr = 72 + (Math.random() * 6).toInt()
                    _uiState.update { state ->
                        state.copy(
                            telemetry = state.telemetry.copy(
                                accelX = jitterX,
                                accelY = jitterY,
                                accelZ = jitterZ,
                                totalG = 1.0f + jitterX * 0.1f,
                                heartRateBpm = hr
                            )
                        )
                    }
                }
            }
        }
    }

    fun setTab(index: Int) {
        _uiState.update { it.copy(currentTab = index) }
    }

    fun selectIncident(incident: Incident) {
        _uiState.update { it.copy(selectedIncident = incident) }
    }

    fun setResponderFilter(filter: String) {
        _uiState.update { it.copy(responderFilter = filter) }
    }

    fun nextSlide() {
        _uiState.update { it.copy(pitchSlideIndex = (it.pitchSlideIndex + 1).coerceAtMost(11)) }
    }

    fun prevSlide() {
        _uiState.update { it.copy(pitchSlideIndex = (it.pitchSlideIndex - 1).coerceAtLeast(0)) }
    }

    fun setSlide(index: Int) {
        _uiState.update { it.copy(pitchSlideIndex = index.coerceIn(0, 11)) }
    }

    // --- ACCESSIBILITY TOGGLES ---
    fun toggleHighContrast() {
        _uiState.update { it.copy(accessibility = it.accessibility.copy(highContrast = !it.accessibility.highContrast)) }
    }

    fun toggleLargeButtons() {
        _uiState.update { it.copy(accessibility = it.accessibility.copy(largeButtons = !it.accessibility.largeButtons)) }
    }

    fun toggleMinimalText() {
        _uiState.update { it.copy(accessibility = it.accessibility.copy(minimalText = !it.accessibility.minimalText)) }
    }

    fun toggleVoiceFeedback() {
        _uiState.update { it.copy(accessibility = it.accessibility.copy(voiceFeedback = !it.accessibility.voiceFeedback)) }
    }

    // --- DIALOG VISIBILITY ---
    fun setAiTransparencyDialog(visible: Boolean) {
        _uiState.update { it.copy(showAiTransparencyDialog = visible) }
    }

    fun setMedicalDialog(visible: Boolean) {
        _uiState.update { it.copy(showMedicalDialog = visible) }
    }

    fun setAddContactDialog(visible: Boolean) {
        _uiState.update { it.copy(showAddContactDialog = visible) }
    }

    fun setDemoScenarioMenu(visible: Boolean) {
        _uiState.update { it.copy(showDemoScenarioMenu = visible) }
    }

    // --- SOS HOLD LOGIC ---
    fun updateSosHoldProgress(progress: Float) {
        _uiState.update { it.copy(sosHoldProgress = progress, isSosHolding = progress > 0f) }
        if (progress >= 1.0f) {
            triggerManualSos()
        }
    }

    fun cancelSosHold() {
        _uiState.update { it.copy(sosHoldProgress = 0f, isSosHolding = false) }
    }

    fun triggerManualSos() {
        _uiState.update { it.copy(sosHoldProgress = 0f, isSosHolding = false) }
        simulateScenario(EmergencyType.MANUAL_SOS, skipCountdown = true)
    }

    // --- SIMULATION / EMERGENCY SCENARIO RUNNER ---
    fun simulateScenario(type: EmergencyType, skipCountdown: Boolean = false) {
        countdownJob?.cancel()

        val timeFormatter = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        val startTimeStr = timeFormatter.format(Date())

        val simulatedTelemetry = when (type) {
            EmergencyType.FALL_DETECTED -> SensorTelemetry(
                accelX = 1.4f, accelY = 2.9f, accelZ = 3.6f, totalG = 4.8f,
                gyroPitch = 48.2f, gyroRoll = 31.0f, inactivitySeconds = 6,
                heartRateBpm = 98, ambientDecibels = 55f, anomalyScore = 0.92f
            )
            EmergencyType.VEHICLE_COLLISION -> SensorTelemetry(
                accelX = 5.2f, accelY = 4.1f, accelZ = 2.1f, totalG = 6.9f,
                gyroPitch = 88.0f, gyroRoll = 72.0f, inactivitySeconds = 8,
                heartRateBpm = 112, ambientDecibels = 82f, anomalyScore = 0.95f
            )
            EmergencyType.VOICE_DISTRESS -> SensorTelemetry(
                accelX = 0.2f, accelY = 0.4f, accelZ = 9.8f, totalG = 1.02f,
                gyroPitch = 5.0f, gyroRoll = 2.0f, inactivitySeconds = 2,
                heartRateBpm = 104, ambientDecibels = 78f, anomalyScore = 0.96f
            )
            EmergencyType.PROLONGED_INACTIVITY -> SensorTelemetry(
                accelX = 0.01f, accelY = 0.02f, accelZ = 9.81f, totalG = 1.0f,
                gyroPitch = 0.1f, gyroRoll = 0.1f, inactivitySeconds = 120,
                heartRateBpm = 54, ambientDecibels = 32f, anomalyScore = 0.88f
            )
            EmergencyType.MANUAL_SOS -> SensorTelemetry(
                accelX = 0.1f, accelY = 0.2f, accelZ = 9.81f, totalG = 1.0f,
                gyroPitch = 12.0f, gyroRoll = 8.0f, inactivitySeconds = 0,
                heartRateBpm = 115, ambientDecibels = 50f, anomalyScore = 0.99f
            )
            EmergencyType.UNKNOWN -> SensorTelemetry()
        }

        val initialAssessment = AiEmergencyEngine.evaluateSignals(
            type = type,
            telemetry = simulatedTelemetry,
            userResponse = if (skipCountdown) "Manual trigger bypass" else "Pending verification"
        )

        val incidentId = "NG-${(10000..99999).random()}"

        val timeline = mutableListOf(
            TimelineEvent("t1", startTimeStr, "${type.label} detected", "Multi-axial sensor anomaly triggered sensory alert threshold", "DETECTION"),
            TimelineEvent("t2", startTimeStr, "AI Sensor Fusion initialized", "Evaluating kinetic vectors, orientation shift, and biometric telemetry", "REASONING")
        )

        val newIncident = Incident(
            id = incidentId,
            subjectName = _uiState.value.medicalProfile.fullName,
            type = type,
            title = type.label,
            status = if (skipCountdown) EmergencyStatus.ESCALATING else EmergencyStatus.VERIFYING,
            severity = initialAssessment.severity,
            confidence = initialAssessment.confidencePercentage,
            detectedTime = startTimeStr,
            locationName = "Microsoft Redmond Campus (Building 99)",
            coordinates = "47.6423° N, 122.1368° W",
            lastResponse = if (skipCountdown) "Manual SOS requested" else "Awaiting user confirmation",
            currentAction = if (skipCountdown) "Autonomous escalation started" else "Presenting multi-modal verification chime",
            aiAssessment = initialAssessment,
            timeline = timeline,
            telemetry = simulatedTelemetry,
            isDemoSimulation = true
        )

        if (skipCountdown) {
            executeEscalation(newIncident)
        } else {
            _uiState.update {
                it.copy(
                    emergencyStatus = EmergencyStatus.VERIFYING,
                    countdownSeconds = 10,
                    activeIncident = newIncident,
                    selectedIncident = newIncident,
                    telemetry = simulatedTelemetry,
                    showEmergencyOverlay = true,
                    statusBanner = "⚠ Simulation Active: ${type.label} detected"
                )
            }

            // Start 10-second countdown
            countdownJob = viewModelScope.launch {
                for (sec in 10 downTo 1) {
                    _uiState.update { it.copy(countdownSeconds = sec) }
                    delay(1000)
                }
                _uiState.update { it.copy(countdownSeconds = 0) }
                // User did not respond! Auto-escalate!
                onCountdownExpired()
            }
        }
    }

    private fun onCountdownExpired() {
        val currentIncident = _uiState.value.activeIncident ?: return
        executeEscalation(currentIncident)
    }

    private fun executeEscalation(incident: Incident) {
        val timeFormatter = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        val escalationTime = timeFormatter.format(Date())

        val postAssessment = AiEmergencyEngine.evaluateSignals(
            type = incident.type,
            telemetry = incident.telemetry,
            userResponse = "No response"
        )

        val updatedTimeline = incident.timeline.toMutableList().apply {
            add(TimelineEvent("t3", escalationTime, "Verification window expired", "No tactile or acoustic response registered from user (10s elapsed)", "VERIFICATION"))
            add(TimelineEvent("t4", escalationTime, "Autonomous Escalation triggered", "AI confidence ${postAssessment.confidencePercentage}% verified: Escalating to trusted safety network", "ESCALATION"))
            add(TimelineEvent("t5", escalationTime, "Trusted Contacts alerted", "Dispatched priority SMS & web hook beacons to 3 emergency contacts", "NOTIFICATION"))
            add(TimelineEvent("t6", escalationTime, "Live Geo-Beacon activated", "Encrypted breadcrumb stream transmitting to responder network", "MONITORING"))
        }

        val updatedContacts = _uiState.value.contacts.map {
            it.copy(isNotified = true, lastContactedTime = escalationTime)
        }

        val escalatedIncident = incident.copy(
            status = EmergencyStatus.DISPATCHED,
            lastResponse = "User did not respond",
            currentAction = "Escalated: Trusted contacts notified & live beacon transmitting",
            aiAssessment = postAssessment,
            timeline = updatedTimeline
        )

        val updatedHistory = listOf(escalatedIncident) + _uiState.value.incidentHistory.filter { it.id != incident.id }

        _uiState.update {
            it.copy(
                emergencyStatus = EmergencyStatus.DISPATCHED,
                activeIncident = escalatedIncident,
                selectedIncident = escalatedIncident,
                incidentHistory = updatedHistory,
                contacts = updatedContacts,
                showEmergencyOverlay = true,
                statusBanner = "EMERGENCY ACTIVE: Autonomous escalation in progress (DEMO MODE)"
            )
        }
    }

    // --- USER OVERRIDES ---
    fun confirmSafe() {
        countdownJob?.cancel()
        val timeFormatter = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        val resolveTime = timeFormatter.format(Date())

        val active = _uiState.value.activeIncident
        if (active != null) {
            val resolvedAssessment = AiEmergencyEngine.evaluateSignals(
                type = active.type,
                telemetry = active.telemetry,
                userResponse = "User confirmed safe"
            )

            val updatedTimeline = active.timeline.toMutableList().apply {
                add(TimelineEvent("t_res", resolveTime, "User confirmed safe", "Tactile override received. Emergency protocol stood down.", "RESOLUTION"))
            }

            val resolvedIncident = active.copy(
                status = EmergencyStatus.RESOLVED,
                lastResponse = "User confirmed safe",
                currentAction = "Incident closed by user",
                aiAssessment = resolvedAssessment,
                timeline = updatedTimeline,
                resolvedAt = resolveTime
            )

            val updatedHistory = listOf(resolvedIncident) + _uiState.value.incidentHistory.filter { it.id != active.id }

            _uiState.update {
                it.copy(
                    emergencyStatus = EmergencyStatus.SAFE,
                    activeIncident = null,
                    selectedIncident = resolvedIncident,
                    incidentHistory = updatedHistory,
                    showEmergencyOverlay = false,
                    statusBanner = "Safe confirmation logged. All alerts cleared."
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    emergencyStatus = EmergencyStatus.SAFE,
                    showEmergencyOverlay = false,
                    statusBanner = "Safety confirmed."
                )
            }
        }
    }

    fun sendHelpImmediately() {
        countdownJob?.cancel()
        val current = _uiState.value.activeIncident ?: return
        executeEscalation(current)
    }

    fun resolveIncident(incidentId: String) {
        val timeFormatter = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        val resolveTime = timeFormatter.format(Date())

        val updatedHistory = _uiState.value.incidentHistory.map { inc ->
            if (inc.id == incidentId) {
                val updatedTimeline = inc.timeline.toMutableList().apply {
                    add(TimelineEvent("t_admin_res", resolveTime, "Incident closed by Responder", "Responder or admin marked incident resolved after contact verification.", "RESOLUTION"))
                }
                inc.copy(
                    status = EmergencyStatus.RESOLVED,
                    currentAction = "Resolved by responder",
                    timeline = updatedTimeline,
                    resolvedAt = resolveTime
                )
            } else inc
        }

        val updatedActive = if (_uiState.value.activeIncident?.id == incidentId) null else _uiState.value.activeIncident

        _uiState.update {
            it.copy(
                incidentHistory = updatedHistory,
                activeIncident = updatedActive,
                selectedIncident = updatedHistory.firstOrNull { inc -> inc.id == incidentId },
                emergencyStatus = if (updatedActive == null) EmergencyStatus.SAFE else it.emergencyStatus,
                statusBanner = "Incident #$incidentId marked as resolved."
            )
        }
    }

    fun addContact(name: String, relationship: String, phone: String) {
        val newContact = EmergencyContact(
            id = "c_${System.currentTimeMillis()}",
            name = name,
            relationship = relationship,
            phone = phone,
            isPrimary = false
        )
        _uiState.update { it.copy(contacts = it.contacts + newContact, showAddContactDialog = false) }
    }

    fun deleteContact(id: String) {
        _uiState.update { it.copy(contacts = it.contacts.filter { c -> c.id != id }) }
    }

    fun updateMedicalProfile(profile: MedicalProfile) {
        _uiState.update { it.copy(medicalProfile = profile, showMedicalDialog = false) }
    }

    private fun createSamplePastIncident(): Incident {
        val initialTelemetry = SensorTelemetry(
            accelX = 0.8f, accelY = 1.9f, accelZ = 2.4f, totalG = 3.2f,
            gyroPitch = 32f, gyroRoll = 21f, inactivitySeconds = 12,
            heartRateBpm = 88, ambientDecibels = 48f, anomalyScore = 0.89f
        )
        val assessment = AiEmergencyEngine.evaluateSignals(
            type = EmergencyType.FALL_DETECTED,
            telemetry = initialTelemetry,
            userResponse = "User confirmed safe"
        )
        val timeline = listOf(
            TimelineEvent("p1", "Yesterday, 3:15:10 PM", "Possible slip/fall detected", "Lateral acceleration spike registered", "DETECTION"),
            TimelineEvent("p2", "Yesterday, 3:15:12 PM", "AI assessment started", "Evaluated impact vectors", "REASONING"),
            TimelineEvent("p3", "Yesterday, 3:15:14 PM", "User verification prompted", "Chime sounded on device", "VERIFICATION"),
            TimelineEvent("p4", "Yesterday, 3:15:20 PM", "User confirmed safe", "Subject pressed 'I Am Safe' button within 6s", "RESOLUTION")
        )
        return Incident(
            id = "NG-48291",
            subjectName = "Alex Chen",
            type = EmergencyType.FALL_DETECTED,
            title = "Possible Fall Event",
            status = EmergencyStatus.RESOLVED,
            severity = SeverityLevel.MEDIUM,
            confidence = 89,
            detectedTime = "Yesterday, 3:15:10 PM",
            locationName = "Microsoft Commons, Redmond WA",
            coordinates = "47.6415° N, 122.1340° W",
            lastResponse = "User confirmed safe",
            currentAction = "Resolved without external dispatch",
            aiAssessment = assessment,
            timeline = timeline,
            telemetry = initialTelemetry,
            isDemoSimulation = true,
            resolvedAt = "Yesterday, 3:15:22 PM"
        )
    }
}
