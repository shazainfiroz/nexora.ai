package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HourglassDisabled
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmergencyType
import com.example.model.SensorTelemetry
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AzureElectric
import com.example.ui.theme.AzureGlow
import com.example.ui.theme.EmergencyCrimson
import com.example.ui.theme.GuardianNavyCard
import com.example.ui.theme.GuardianNavyDark
import com.example.ui.theme.SafeEmerald

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DemoControlPanel(
    telemetry: SensorTelemetry,
    onSimulateScenario: (EmergencyType) -> Unit,
    onSimulateNoResponse: () -> Unit,
    onConfirmSafe: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = GuardianNavyCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, AlertOrange.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("demo_control_panel")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with DEMO SIMULATION badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Demo Simulation Engine",
                        tint = AlertOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DEMO SIMULATION SUITE",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AlertOrange.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AlertOrange)
                ) {
                    Text(
                        text = "JUDGE BENCH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = AlertOrange,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Simulate real-world emergency sensory telemetry without physical hardware. AI reasoning model responds instantly.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Live Sensor Telemetry readout pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = GuardianNavyDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "G-Force: ${String.format("%.2f", telemetry.totalG)}G",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                        color = if (telemetry.totalG > 2.5f) EmergencyCrimson else AzureGlow
                    )
                    Text(
                        text = "Pitch: ${String.format("%.1f", telemetry.gyroPitch)}°",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "BPM: ${telemetry.heartRateBpm}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (telemetry.heartRateBpm > 100) EmergencyCrimson else SafeEmerald
                    )
                    Text(
                        text = "Inact: ${telemetry.inactivitySeconds}s",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (telemetry.inactivitySeconds > 10) AlertOrange else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons in FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. SIMULATE FALL
                Button(
                    onClick = { onSimulateScenario(EmergencyType.FALL_DETECTED) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF202E4E)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_simulate_fall")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = AlertOrange, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SIMULATE FALL", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                }

                // 2. SIMULATE NO RESPONSE
                Button(
                    onClick = onSimulateNoResponse,
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyCrimson.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyCrimson),
                    modifier = Modifier.testTag("btn_simulate_no_response")
                ) {
                    Icon(imageVector = Icons.Default.HourglassDisabled, contentDescription = null, tint = EmergencyCrimson, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SIMULATE NO RESPONSE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = EmergencyCrimson)
                }

                // 3. SIMULATE VOICE SOS
                Button(
                    onClick = { onSimulateScenario(EmergencyType.VOICE_DISTRESS) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF202E4E)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_simulate_voice_sos")
                ) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = AzureGlow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("VOICE SOS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                }

                // 4. SIMULATE ACCIDENT / VEHICLE
                Button(
                    onClick = { onSimulateScenario(EmergencyType.VEHICLE_COLLISION) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF202E4E)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_simulate_accident")
                ) {
                    Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = AlertOrange, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SIMULATE ACCIDENT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                }

                // 5. USER SAFE / RESET
                OutlinedButton(
                    onClick = onConfirmSafe,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SafeEmerald),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SafeEmerald),
                    modifier = Modifier.testTag("btn_user_safe")
                ) {
                    Icon(imageVector = Icons.Default.PowerSettingsNew, contentDescription = null, tint = SafeEmerald, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("USER SAFE (RESET)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SafeEmerald)
                }
            }
        }
    }
}
private val TextPrimary = Color(0xFFF8FAFC)
