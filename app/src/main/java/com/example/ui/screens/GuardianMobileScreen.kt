package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmergencyContact
import com.example.model.EmergencyStatus
import com.example.model.EmergencyType
import com.example.model.MedicalProfile
import com.example.model.SensorTelemetry
import com.example.ui.components.DemoControlPanel
import com.example.ui.components.SafetyShieldPulse
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AzureElectric
import com.example.ui.theme.AzureGlow
import com.example.ui.theme.EmergencyCrimson
import com.example.ui.theme.EmergencyCrimsonDark
import com.example.ui.theme.GuardianNavyCard
import com.example.ui.theme.GuardianNavyDark
import com.example.ui.theme.SafeEmerald
import kotlinx.coroutines.delay

@Composable
fun GuardianMobileScreen(
    status: EmergencyStatus,
    telemetry: SensorTelemetry,
    contacts: List<EmergencyContact>,
    medicalProfile: MedicalProfile,
    minimalText: Boolean,
    largeButtons: Boolean,
    onSimulateScenario: (EmergencyType) -> Unit,
    onSimulateNoResponse: () -> Unit,
    onConfirmSafe: () -> Unit,
    onManualSos: () -> Unit,
    onOpenMedical: () -> Unit,
    onAddContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    var holdProgress by remember { mutableFloatStateOf(0f) }
    var isHolding by remember { mutableStateOf(false) }

    // Smooth hold animation
    LaunchedEffect(isHolding) {
        if (isHolding) {
            val startTime = System.currentTimeMillis()
            val holdDuration = 1800L // 1.8 seconds intentional hold
            while (isHolding) {
                val elapsed = System.currentTimeMillis() - startTime
                holdProgress = (elapsed.toFloat() / holdDuration).coerceIn(0f, 1f)
                if (holdProgress >= 1f) {
                    isHolding = false
                    holdProgress = 0f
                    onManualSos()
                    break
                }
                delay(30)
            }
        } else {
            holdProgress = 0f
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. TOP CENTRAL SAFETY STATUS & SHIELD
        item {
            SafetyShieldPulse(
                status = status,
                activeContactsCount = contacts.size,
                minimalText = minimalText
            )
        }

        // 2. MAIN ACTIONS: [ HOLD FOR SOS ] & [ I AM SAFE ] & [ TEST EMERGENCY ]
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // HOLD FOR SOS BUTTON (Circular deliberate gesture to prevent accidental trigger)
                Box(
                    modifier = Modifier
                        .size(if (largeButtons) 190.dp else 160.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isHolding = true
                                    tryAwaitRelease()
                                    isHolding = false
                                }
                            )
                        }
                        .testTag("hold_for_sos_button"),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer progress track ring
                    CircularProgressIndicator(
                        progress = { holdProgress.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxSize(),
                        color = EmergencyCrimson,
                        trackColor = EmergencyCrimsonDark.copy(alpha = 0.3f),
                        strokeWidth = 8.dp
                    )

                    // Inner Hold Button
                    Box(
                        modifier = Modifier
                            .size(if (largeButtons) 160.dp else 134.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(EmergencyCrimson, EmergencyCrimsonDark)
                                )
                            )
                            .border(2.dp, Color(0xFFFF4D79), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Emergency,
                                contentDescription = "Emergency SOS",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isHolding) "HOLDING..." else "HOLD FOR SOS",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = if (isHolding) "${(holdProgress * 100).toInt()}%" else "PRESS 2 SEC",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // [ I AM SAFE ] and [ TEST EMERGENCY ]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onConfirmSafe,
                        colors = ButtonDefaults.buttonColors(containerColor = SafeEmerald),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(if (largeButtons) 56.dp else 48.dp)
                            .testTag("i_am_safe_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I AM SAFE",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }

                    OutlinedButton(
                        onClick = { onSimulateScenario(EmergencyType.FALL_DETECTED) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertOrange),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AlertOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(if (largeButtons) 56.dp else 48.dp)
                            .testTag("test_emergency_button")
                    ) {
                        Icon(imageVector = Icons.Default.Sensors, contentDescription = null, tint = AlertOrange, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TEST EMERGENCY",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = AlertOrange
                        )
                    }
                }
            }
        }

        // 3. DEMO SIMULATION ENGINE (Required by prompt)
        item {
            DemoControlPanel(
                telemetry = telemetry,
                onSimulateScenario = onSimulateScenario,
                onSimulateNoResponse = onSimulateNoResponse,
                onConfirmSafe = onConfirmSafe
            )
        }

        // 4. EMERGENCY CONTACTS CIRCLE
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GuardianNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF233252)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = AzureGlow, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Emergency Contacts (${contacts.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(onClick = onAddContact, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Contact", tint = AzureGlow)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    contacts.forEach { contact ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GuardianNavyDark,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (contact.isPrimary) AzureElectric.copy(alpha = 0.25f) else Color(0xFF22304C)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (contact.isPrimary) AzureGlow else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = contact.name,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (contact.isPrimary) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = SafeEmerald.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = "PRIMARY",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                                        color = SafeEmerald,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${contact.relationship} • ${contact.phone}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (contact.isNotified) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AlertOrange.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "ALERTED",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                            color = AlertOrange,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "Test Call",
                                        tint = SafeEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. MEDICAL INFORMATION PROFILE CARD
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GuardianNavyCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF233252)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.MedicalInformation, contentDescription = null, tint = EmergencyCrimson, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Emergency Medical ID",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = onOpenMedical,
                            colors = ButtonDefaults.buttonColors(containerColor = AzureElectric.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("VIEW / EDIT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = AzureGlow)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Subject", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "${medicalProfile.fullName} (${medicalProfile.age} yrs)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                        }
                        Column {
                            Text(text = "Blood Type", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = medicalProfile.bloodType, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = EmergencyCrimson)
                        }
                        Column {
                            Text(text = "Organ Donor", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = if (medicalProfile.organDonor) "YES" else "NO", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = SafeEmerald)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Allergies: ${medicalProfile.allergies} • Notes: ${medicalProfile.emergencyNotes}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
