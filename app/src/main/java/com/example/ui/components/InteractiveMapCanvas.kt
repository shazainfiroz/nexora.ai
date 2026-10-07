package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FacilityPin
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AzureElectric
import com.example.ui.theme.AzureGlow
import com.example.ui.theme.EmergencyCrimson
import com.example.ui.theme.GuardianNavyCard
import com.example.ui.theme.GuardianNavyDark
import com.example.ui.theme.SafeEmerald

@Composable
fun InteractiveMapCanvas(
    userCoordinates: String,
    facilities: List<FacilityPin>,
    isEmergency: Boolean = false,
    modifier: Modifier = Modifier
) {
    var privacyModeEnabled by remember { mutableStateOf(false) }
    var selectedFacility by remember { mutableStateOf<FacilityPin?>(facilities.firstOrNull()) }

    // Radar rotation
    val infiniteTransition = rememberInfiniteTransition(label = "map_radar")
    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = GuardianNavyDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF233252)),
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .testTag("interactive_map_canvas")
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val widthPx = constraints.maxWidth.toFloat()
            val heightPx = constraints.maxHeight.toFloat()

            // User normalized center
            val userCenter = Offset(widthPx * 0.48f, heightPx * 0.52f)

            // Vector Canvas Drawing
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            // Check which facility was tapped
                            val hitFacility = facilities.minByOrNull { f ->
                                val fx = widthPx * f.xNorm
                                val fy = heightPx * f.yNorm
                                val dist = (tapOffset.x - fx) * (tapOffset.x - fx) + (tapOffset.y - fy) * (tapOffset.y - fy)
                                dist
                            }
                            selectedFacility = hitFacility
                        }
                    }
            ) {
                // Background grid lines (Azure Maps Vector Style)
                val gridStep = 45f
                for (x in 0..(size.width / gridStep).toInt()) {
                    drawLine(
                        color = Color(0xFF131D33),
                        start = Offset(x * gridStep, 0f),
                        end = Offset(x * gridStep, size.height),
                        strokeWidth = 1f
                    )
                }
                for (y in 0..(size.height / gridStep).toInt()) {
                    drawLine(
                        color = Color(0xFF131D33),
                        start = Offset(0f, y * gridStep),
                        end = Offset(size.width, y * gridStep),
                        strokeWidth = 1f
                    )
                }

                // Simulated Road Arteries
                val roadColor = Color(0xFF1A2744)
                drawLine(
                    color = roadColor,
                    start = Offset(0f, size.height * 0.45f),
                    end = Offset(size.width, size.height * 0.65f),
                    strokeWidth = 6f
                )
                drawLine(
                    color = roadColor,
                    start = Offset(size.width * 0.35f, 0f),
                    end = Offset(size.width * 0.6f, size.height),
                    strokeWidth = 5f
                )

                // Dispatch Route Path from Facility to User
                selectedFacility?.let { fac ->
                    val facOffset = Offset(size.width * fac.xNorm, size.height * fac.yNorm)
                    val routePath = Path().apply {
                        moveTo(facOffset.x, facOffset.y)
                        val midX = (facOffset.x + userCenter.x) / 2
                        val midY = (facOffset.y + userCenter.y) / 2 + 25f
                        quadraticTo(midX, midY, userCenter.x, userCenter.y)
                    }
                    drawPath(
                        path = routePath,
                        color = if (isEmergency) EmergencyCrimson else AzureGlow,
                        style = Stroke(
                            width = 3f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    )
                }

                // Range Circles around User
                drawCircle(
                    color = (if (isEmergency) EmergencyCrimson else AzureElectric).copy(alpha = 0.15f),
                    radius = size.width * 0.22f,
                    center = userCenter,
                    style = Stroke(width = 1.5f)
                )
                drawCircle(
                    color = (if (isEmergency) EmergencyCrimson else AzureElectric).copy(alpha = 0.08f),
                    radius = size.width * 0.38f,
                    center = userCenter,
                    style = Stroke(width = 1.5f)
                )

                // User Location Dot with Radar Pulse
                val userDotColor = if (isEmergency) EmergencyCrimson else SafeEmerald
                drawCircle(
                    color = userDotColor.copy(alpha = 0.3f),
                    radius = if (privacyModeEnabled) 36f else 18f,
                    center = userCenter
                )
                drawCircle(
                    color = userDotColor,
                    radius = 7f,
                    center = userCenter
                )
            }

            // Overlay Markers (HTML/Compose UI overlay)
            // 1. User Tag
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 34.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isEmergency) EmergencyCrimson else SafeEmerald,
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = if (privacyModeEnabled) "COARSE ZONE (~500m)" else "USER: LIVE GPS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // 2. Facilities as floating icon pins
            facilities.forEach { facility ->
                val xFraction = facility.xNorm
                val yFraction = facility.yNorm
                val isSelected = selectedFacility?.id == facility.id

                val xOffset = (maxWidth * xFraction - 16.dp).coerceIn(0.dp, maxWidth - 36.dp)
                val yOffset = (maxHeight * yFraction - 16.dp).coerceIn(0.dp, maxHeight - 36.dp)

                Box(
                    modifier = Modifier.offset(x = xOffset, y = yOffset)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) AzureGlow else GuardianNavyCard,
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (facility.type == "HOSPITAL") EmergencyCrimson else AzureElectric
                        ),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { selectedFacility = facility }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (facility.type == "HOSPITAL") Icons.Default.LocalHospital else Icons.Default.LocalPolice,
                                contentDescription = facility.name,
                                tint = if (isSelected) Color.Black else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Top Info Bar (Demo Badge + Privacy Controls)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GuardianNavyCard.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B3A60))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            tint = AzureGlow,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (privacyModeEnabled) "PRIVACY ACTIVE • COARSE LOCATION" else "AZURE MAPS DEMO • REDMOND HQ",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.SemiBold),
                            color = AzureGlow
                        )
                    }
                }

                // Privacy Switch Icon
                Surface(
                    shape = CircleShape,
                    color = GuardianNavyCard.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B3A60))
                ) {
                    IconButton(
                        onClick = { privacyModeEnabled = !privacyModeEnabled },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (privacyModeEnabled) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle location privacy fuzzing",
                            tint = if (privacyModeEnabled) AlertOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Bottom Selected Facility Info Card
            selectedFacility?.let { fac ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GuardianNavyCard.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B3A60)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = fac.name,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Dist: ${fac.distanceKm} km • Est. ETA: ${fac.etaMinutes} mins",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AzureElectric.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AzureElectric)
                        ) {
                            Text(
                                text = "ROUTE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                color = AzureGlow,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
