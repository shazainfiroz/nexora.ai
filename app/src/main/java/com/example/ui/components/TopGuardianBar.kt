package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmergencyStatus
import com.example.model.EmergencyType
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AzureGlow
import com.example.ui.theme.EmergencyCrimson
import com.example.ui.theme.GuardianNavyCard
import com.example.ui.theme.SafeEmerald

@Composable
fun TopGuardianBar(
    status: EmergencyStatus,
    onSimulateScenario: (EmergencyType) -> Unit,
    onToggleHighContrast: () -> Unit,
    onToggleLargeButtons: () -> Unit,
    onToggleMinimalText: () -> Unit,
    onOpenAiTransparency: () -> Unit,
    onOpenMedical: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSimMenu by remember { mutableStateOf(false) }
    var showA11yMenu by remember { mutableStateOf(false) }

    Surface(
        color = GuardianNavyCard,
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo & Branding
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AzureGlow.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Nexora Guardian Security Logo",
                            tint = AzureGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NEXORA",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GUARDIAN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = AzureGlow
                            )
                        }
                        Text(
                            text = "AI Emergency Intelligence",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Action controls: Simulation Menu, Accessibility, Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Demo Simulation Trigger Pill
                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AlertOrange.copy(alpha = 0.18f),
                            modifier = Modifier
                                .clickable { showSimMenu = true }
                                .testTag("sim_menu_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Simulate",
                                    tint = AlertOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "DEMO SIM",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = AlertOrange
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showSimMenu,
                            onDismissRequest = { showSimMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("⚡ Simulate Sudden Fall (4.8G)") },
                                onClick = {
                                    showSimMenu = false
                                    onSimulateScenario(EmergencyType.FALL_DETECTED)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("⚡ Simulate Voice SOS ('Help!')") },
                                onClick = {
                                    showSimMenu = false
                                    onSimulateScenario(EmergencyType.VOICE_DISTRESS)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("⚡ Simulate Vehicle Deceleration") },
                                onClick = {
                                    showSimMenu = false
                                    onSimulateScenario(EmergencyType.VEHICLE_COLLISION)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("⚡ Simulate Prolonged Inactivity") },
                                onClick = {
                                    showSimMenu = false
                                    onSimulateScenario(EmergencyType.PROLONGED_INACTIVITY)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Medical Info
                    IconButton(
                        onClick = onOpenMedical,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("medical_info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = "Medical ID",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Accessibility Menu
                    Box {
                        IconButton(
                            onClick = { showA11yMenu = true },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("accessibility_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessibilityNew,
                                contentDescription = "Accessibility Options",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showA11yMenu,
                            onDismissRequest = { showA11yMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Toggle High Contrast Mode") },
                                onClick = {
                                    showA11yMenu = false
                                    onToggleHighContrast()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Toggle Large Buttons") },
                                onClick = {
                                    showA11yMenu = false
                                    onToggleLargeButtons()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Toggle Minimal Text Mode") },
                                onClick = {
                                    showA11yMenu = false
                                    onToggleMinimalText()
                                }
                            )
                        }
                    }

                    // AI Transparency modal button
                    IconButton(
                        onClick = onOpenAiTransparency,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("ai_transparency_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "AI Transparency & Responsible AI",
                            tint = AzureGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Status Banner indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (statusColor, statusText) = when (status) {
                    EmergencyStatus.SAFE -> Pair(SafeEmerald, "PROTECTION: ACTIVE • 24/7 SENSORS ONLINE")
                    EmergencyStatus.VERIFYING -> Pair(AlertOrange, "⚠ POSSIBLE EMERGENCY DETECTED • VERIFYING")
                    EmergencyStatus.ESCALATING, EmergencyStatus.DISPATCHED -> Pair(EmergencyCrimson, "🚨 EMERGENCY ACTIVE • AUTONOMOUS ESCALATION")
                    EmergencyStatus.RESOLVED -> Pair(SafeEmerald, "INCIDENT RESOLVED • MONITORING")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = statusColor
                    )
                }

                Text(
                    text = "MICROSOFT AZURE CLOUD READY",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
