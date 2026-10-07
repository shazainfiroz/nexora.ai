package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmergencyStatus
import com.example.ui.components.AddContactDialog
import com.example.ui.components.AiTransparencyDialog
import com.example.ui.components.EmergencyOverlay
import com.example.ui.components.MedicalProfileDialog
import com.example.ui.components.TopGuardianBar
import com.example.ui.screens.AnalyticsDashboardScreen
import com.example.ui.screens.GuardianMobileScreen
import com.example.ui.screens.PitchAndDocsScreen
import com.example.ui.screens.ResponderDashboardScreen
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AzureElectric
import com.example.ui.theme.AzureGlow
import com.example.ui.theme.EmergencyCrimson
import com.example.ui.theme.GuardianNavyCard
import com.example.ui.theme.GuardianNavyDark
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.NexoraViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: NexoraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            MyApplicationTheme(highContrast = uiState.accessibility.highContrast) {
                // Back handler pops overlay or switches back to tab 0
                BackHandler(enabled = uiState.showEmergencyOverlay || uiState.currentTab != 0) {
                    if (uiState.showEmergencyOverlay) {
                        viewModel.confirmSafe()
                    } else if (uiState.currentTab != 0) {
                        viewModel.setTab(0)
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(GuardianNavyDark),
                        topBar = {
                            TopGuardianBar(
                                status = uiState.emergencyStatus,
                                onSimulateScenario = { viewModel.simulateScenario(it) },
                                onToggleHighContrast = { viewModel.toggleHighContrast() },
                                onToggleLargeButtons = { viewModel.toggleLargeButtons() },
                                onToggleMinimalText = { viewModel.toggleMinimalText() },
                                onOpenAiTransparency = { viewModel.setAiTransparencyDialog(true) },
                                onOpenMedical = { viewModel.setMedicalDialog(true) }
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = GuardianNavyCard,
                                contentColor = AzureGlow,
                                tonalElevation = 8.dp
                            ) {
                                // 1. Mobile Guardian Tab
                                NavigationBarItem(
                                    selected = uiState.currentTab == 0,
                                    onClick = { viewModel.setTab(0) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = "Guardian Safety App",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = { Text("Guardian", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = AzureGlow,
                                        indicatorColor = AzureElectric,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("tab_guardian")
                                )

                                // 2. Responder Dashboard Tab
                                NavigationBarItem(
                                    selected = uiState.currentTab == 1,
                                    onClick = { viewModel.setTab(1) },
                                    icon = {
                                        val activeCount = uiState.incidentHistory.count { it.status != EmergencyStatus.RESOLVED && it.status != EmergencyStatus.SAFE }
                                        if (activeCount > 0) {
                                            BadgedBox(badge = { Badge { Text("$activeCount") } }) {
                                                Icon(
                                                    imageVector = Icons.Default.Emergency,
                                                    contentDescription = "Responder Portal",
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Emergency,
                                                contentDescription = "Responder Portal",
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    },
                                    label = { Text("Responders", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = AzureGlow,
                                        indicatorColor = AzureElectric,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("tab_responders")
                                )

                                // 3. Analytics Dashboard Tab
                                NavigationBarItem(
                                    selected = uiState.currentTab == 2,
                                    onClick = { viewModel.setTab(2) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Assessment,
                                            contentDescription = "Analytics & Telemetry",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = { Text("Analytics", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = AzureGlow,
                                        indicatorColor = AzureElectric,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("tab_analytics")
                                )

                                // 4. Pitch & Docs Tab
                                NavigationBarItem(
                                    selected = uiState.currentTab == 3,
                                    onClick = { viewModel.setTab(3) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Slideshow,
                                            contentDescription = "Pitch & Docs",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = { Text("Pitch & Docs", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = AzureGlow,
                                        indicatorColor = AzureElectric,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("tab_pitch")
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(GuardianNavyDark)
                        ) {
                            when (uiState.currentTab) {
                                0 -> GuardianMobileScreen(
                                    status = uiState.emergencyStatus,
                                    telemetry = uiState.telemetry,
                                    contacts = uiState.contacts,
                                    medicalProfile = uiState.medicalProfile,
                                    minimalText = uiState.accessibility.minimalText,
                                    largeButtons = uiState.accessibility.largeButtons,
                                    onSimulateScenario = { viewModel.simulateScenario(it) },
                                    onSimulateNoResponse = { viewModel.simulateScenario(com.example.model.EmergencyType.FALL_DETECTED, skipCountdown = false) },
                                    onConfirmSafe = { viewModel.confirmSafe() },
                                    onManualSos = { viewModel.triggerManualSos() },
                                    onOpenMedical = { viewModel.setMedicalDialog(true) },
                                    onAddContact = { viewModel.setAddContactDialog(true) }
                                )

                                1 -> ResponderDashboardScreen(
                                    incidents = uiState.incidentHistory,
                                    selectedIncident = uiState.selectedIncident,
                                    facilities = uiState.facilities,
                                    filter = uiState.responderFilter,
                                    onFilterChange = { viewModel.setResponderFilter(it) },
                                    onSelectIncident = { viewModel.selectIncident(it) },
                                    onResolveIncident = { viewModel.resolveIncident(it) }
                                )

                                2 -> AnalyticsDashboardScreen(
                                    analytics = uiState.analytics
                                )

                                3 -> PitchAndDocsScreen(
                                    currentSlideIndex = uiState.pitchSlideIndex,
                                    onNextSlide = { viewModel.nextSlide() },
                                    onPrevSlide = { viewModel.prevSlide() },
                                    onSetSlide = { viewModel.setSlide(it) }
                                )
                            }
                        }
                    }

                    // EMERGENCY OVERLAY (When countdown is running or emergency is actively escalated)
                    if (uiState.showEmergencyOverlay) {
                        EmergencyOverlay(
                            incident = uiState.activeIncident,
                            countdownSeconds = uiState.countdownSeconds,
                            status = uiState.emergencyStatus,
                            onConfirmSafe = { viewModel.confirmSafe() },
                            onSendHelpImmediately = { viewModel.sendHelpImmediately() }
                        )
                    }

                    // DIALOGS
                    if (uiState.showAiTransparencyDialog) {
                        AiTransparencyDialog(
                            onDismiss = { viewModel.setAiTransparencyDialog(false) }
                        )
                    }

                    if (uiState.showMedicalDialog) {
                        MedicalProfileDialog(
                            profile = uiState.medicalProfile,
                            onSave = { viewModel.updateMedicalProfile(it) },
                            onDismiss = { viewModel.setMedicalDialog(false) }
                        )
                    }

                    if (uiState.showAddContactDialog) {
                        AddContactDialog(
                            onAdd = { name, rel, phone -> viewModel.addContact(name, rel, phone) },
                            onDismiss = { viewModel.setAddContactDialog(false) }
                        )
                    }
                }
            }
        }
    }
}
