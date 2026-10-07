package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmergencyStatus
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AzureElectric
import com.example.ui.theme.AzureGlow
import com.example.ui.theme.EmergencyCrimson
import com.example.ui.theme.GuardianNavyCard
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.SafeEmeraldGlow

@Composable
fun SafetyShieldPulse(
    status: EmergencyStatus,
    activeContactsCount: Int = 3,
    minimalText: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isEmergency = status == EmergencyStatus.ESCALATING || status == EmergencyStatus.DISPATCHED
    val isVerifying = status == EmergencyStatus.VERIFYING

    // Pulsing animation for safety / alert aura
    val infiniteTransition = rememberInfiniteTransition(label = "shield_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isEmergency) 1.25f else 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isEmergency) 700 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = if (isEmergency) 0.5f else 0.3f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isEmergency) 700 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val auraColor = when {
        isEmergency -> EmergencyCrimson
        isVerifying -> AlertOrange
        else -> SafeEmeraldGlow
    }

    val primaryColor = when {
        isEmergency -> EmergencyCrimson
        isVerifying -> AlertOrange
        else -> SafeEmerald
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing Orb & Shield Icon
        Box(
            modifier = Modifier.size(170.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer Pulsing Ripple 2
            Box(
                modifier = Modifier
                    .size(165.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(auraColor.copy(alpha = pulseAlpha))
            )

            // Outer Pulsing Ripple 1
            Box(
                modifier = Modifier
                    .size(135.dp)
                    .scale((pulseScale + 1f) / 2f)
                    .clip(CircleShape)
                    .background(auraColor.copy(alpha = pulseAlpha * 1.5f))
            )

            // Inner Central Shield Container
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.28f),
                                GuardianNavyCard
                            )
                        )
                    )
                    .border(2.dp, primaryColor.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isEmergency -> Icons.Default.Warning
                        isVerifying -> Icons.Default.Warning
                        else -> Icons.Default.Shield
                    },
                    contentDescription = "Shield Status Indicator",
                    tint = primaryColor,
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Large central safety status text
        Text(
            text = when {
                isEmergency -> "EMERGENCY ACTIVE"
                isVerifying -> "VERIFYING SAFETY..."
                else -> "YOU ARE SAFE"
            },
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            ),
            color = primaryColor
        )

        if (!minimalText) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when {
                    isEmergency -> "Autonomous escalation triggered • Emergency beacons active"
                    isVerifying -> "Sensory anomaly flagged • Verification countdown active"
                    else -> "Nexora AI is actively safeguarding your movement & acoustic context"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4 System Status Metrics (Required by prompt)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = GuardianNavyCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF233252)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatusItem(
                    icon = Icons.Default.CheckCircle,
                    label = "Protection",
                    value = "ACTIVE",
                    color = SafeEmerald
                )
                StatusItem(
                    icon = Icons.Default.LocationOn,
                    label = "Location",
                    value = "ACTIVE",
                    color = AzureGlow
                )
                StatusItem(
                    icon = Icons.Default.ContactPhone,
                    label = "Contacts",
                    value = "$activeContactsCount",
                    color = AzureElectric
                )
                StatusItem(
                    icon = Icons.Default.Psychology,
                    label = "AI Monitor",
                    value = "ACTIVE",
                    color = SafeEmerald
                )
            }
        }
    }
}

@Composable
private fun StatusItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
            color = color
        )
    }
}
