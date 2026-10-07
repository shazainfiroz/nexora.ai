package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmergencyStatus
import com.example.model.Incident
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AzureGlow
import com.example.ui.theme.EmergencyCrimson
import com.example.ui.theme.EmergencyCrimsonDark
import com.example.ui.theme.GuardianNavyCard
import com.example.ui.theme.GuardianNavyDark
import com.example.ui.theme.GuardianNavyElevated
import com.example.ui.theme.SafeEmerald

@Composable
fun EmergencyOverlay(
    incident: Incident?,
    countdownSeconds: Int,
    status: EmergencyStatus,
    onConfirmSafe: () -> Unit,
    onSendHelpImmediately: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (incident == null) return

    val isVerifying = status == EmergencyStatus.VERIFYING
    val isEscalated = status == EmergencyStatus.ESCALATING || status == EmergencyStatus.DISPATCHED

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_alert")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alert_glow"
    )

    Surface(
        color = GuardianNavyDark.copy(alpha = 0.98f),
        modifier = modifier
            .fillMaxSize()
            .testTag("emergency_overlay_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // DEMO MODE BADGE
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AlertOrange.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AlertOrange)
            ) {
                Text(
                    text = "DEMO SIMULATION • NO ACTUAL 911 CALL MADE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = AlertOrange,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isVerifying) {
                // 1. VERIFICATION COUNTDOWN SCREEN
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { ((10 - countdownSeconds) / 10f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxSize(),
                        color = EmergencyCrimson,
                        trackColor = Color(0xFF331122),
                        strokeWidth = 10.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$countdownSeconds",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 56.sp
                            ),
                            color = EmergencyCrimson
                        )
                        Text(
                            text = "SECONDS",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alert",
                        tint = EmergencyCrimson,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "POSSIBLE EMERGENCY DETECTED",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = EmergencyCrimson
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "“Are you okay?”",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Nexora registered abnormal motion (${incident.type.label}). If no response is received, escalation will begin automatically.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // AI Reason Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GuardianNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B3A60)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "AI",
                                tint = AzureGlow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI Reasoning (${incident.confidence}% Confidence)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = AzureGlow
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        incident.aiAssessment.reasoningPoints.forEach { point ->
                            Text(
                                text = "• $point",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons: [ I'M SAFE ] and [ SEND HELP ]
                Button(
                    onClick = onConfirmSafe,
                    colors = ButtonDefaults.buttonColors(containerColor = SafeEmerald),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("verify_i_am_safe_button")
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I'M SAFE (CANCEL)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onSendHelpImmediately,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EmergencyCrimson),
                    border = androidx.compose.foundation.BorderStroke(2.dp, EmergencyCrimson),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("verify_send_help_button")
                ) {
                    Icon(imageVector = Icons.Default.Emergency, contentDescription = null, tint = EmergencyCrimson)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SEND HELP NOW",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = EmergencyCrimson
                    )
                }
            } else if (isEscalated) {
                // 2. ESCALATED STATE (User did not respond or requested immediate help)
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(EmergencyCrimson.copy(alpha = alphaAnim))
                        .border(3.dp, EmergencyCrimson, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Active Emergency Beacon",
                        tint = EmergencyCrimson,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "EMERGENCY PROTOCOL ACTIVE",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    ),
                    color = EmergencyCrimson
                )

                Text(
                    text = "User did not respond within verification window.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Status details card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = GuardianNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF332035)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Incident ID",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "#${incident.id}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = AzureGlow
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Emergency status",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "ACTIVE",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmergencyCrimson
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Location Beacon",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "LIVE (${incident.coordinates})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = SafeEmerald
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Trusted contacts",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "NOTIFIED (3 alerts sent)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = AlertOrange
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stand-down / I'm Safe Button
                Button(
                    onClick = onConfirmSafe,
                    colors = ButtonDefaults.buttonColors(containerColor = SafeEmerald),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("escalated_confirm_safe_button")
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "I AM SAFE NOW (RESOLVE INCIDENT)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = Color.Black
                    )
                }
            }
        }
    }
}
