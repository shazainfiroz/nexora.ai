package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.model.EmergencyStatus
import com.example.model.FacilityPin
import com.example.model.Incident
import com.example.model.SeverityLevel
import com.example.ui.components.InteractiveMapCanvas
import com.example.ui.components.TimelineView
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AzureElectric
import com.example.ui.theme.AzureGlow
import com.example.ui.theme.EmergencyCrimson
import com.example.ui.theme.GuardianNavyCard
import com.example.ui.theme.GuardianNavyDark
import com.example.ui.theme.GuardianNavyElevated
import com.example.ui.theme.SafeEmerald

@Composable
fun ResponderDashboardScreen(
    incidents: List<Incident>,
    selectedIncident: Incident?,
    facilities: List<FacilityPin>,
    filter: String,
    onFilterChange: (String) -> Unit,
    onSelectIncident: (Incident) -> Unit,
    onResolveIncident: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredIncidents = when (filter) {
        "ACTIVE" -> incidents.filter { it.status != EmergencyStatus.RESOLVED && it.status != EmergencyStatus.SAFE }
        "RESOLVED" -> incidents.filter { it.status == EmergencyStatus.RESOLVED }
        else -> incidents
    }

    val activeToDisplay = selectedIncident ?: filteredIncidents.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Dashboard Header & Filter Chips
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EMERGENCY RESPONDER PORTAL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Real-time dispatch triage & sensor telematics review",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AzureElectric.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AzureElectric)
                    ) {
                        Text(
                            text = "ROLE: TRUSTED RESPONDER",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                            color = AzureGlow,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("ALL", "ACTIVE", "RESOLVED").forEach { f ->
                        FilterChip(
                            selected = filter == f,
                            onClick = { onFilterChange(f) },
                            label = { Text(f, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AzureElectric,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // 2. Incident Selection Carousel
        item {
            Column {
                Text(
                    text = "INCIDENT FEED (${filteredIncidents.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (filteredIncidents.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GuardianNavyCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No incidents in this view. Use the Demo Simulation panel to trigger an event.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredIncidents) { inc ->
                            val isSelected = activeToDisplay?.id == inc.id
                            val isCritical = inc.severity == SeverityLevel.CRITICAL || inc.severity == SeverityLevel.HIGH

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) GuardianNavyElevated else GuardianNavyCard,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) AzureGlow else Color(0xFF233252)
                                ),
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable { onSelectIncident(inc) }
                                    .testTag("incident_card_${inc.id}")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "#${inc.id}",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                            color = AzureGlow
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (inc.status == EmergencyStatus.RESOLVED) SafeEmerald.copy(alpha = 0.2f) else EmergencyCrimson.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = if (inc.status == EmergencyStatus.RESOLVED) "RESOLVED" else "ACTIVE",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                                color = if (inc.status == EmergencyStatus.RESOLVED) SafeEmerald else EmergencyCrimson,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = inc.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${inc.detectedTime} • AI: ${inc.confidence}%",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Detailed Selected Incident View
        if (activeToDisplay != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = GuardianNavyCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B3A60)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Title header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "INCIDENT #${activeToDisplay.id}",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                                    color = AzureGlow
                                )
                                Text(
                                    text = "${activeToDisplay.title} • Subject: ${activeToDisplay.subjectName}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (activeToDisplay.severity == SeverityLevel.CRITICAL) EmergencyCrimson.copy(alpha = 0.2f) else AlertOrange.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${activeToDisplay.severity} PRIORITY",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (activeToDisplay.severity == SeverityLevel.CRITICAL) EmergencyCrimson else AlertOrange,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Grid stats: AI Confidence, Last Response, Detected Time, Current Action
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "AI Confidence", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${activeToDisplay.confidence}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = AzureGlow)
                            }
                            Column {
                                Text(text = "Last Response", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = activeToDisplay.lastResponse, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = if (activeToDisplay.lastResponse.contains("No response")) EmergencyCrimson else SafeEmerald)
                            }
                            Column {
                                Text(text = "Detected Time", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = activeToDisplay.detectedTime, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Current Action: ${activeToDisplay.currentAction}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = AlertOrange
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Map with nearby facilities
                        Text(
                            text = "Live Incident Radar & Spatial Facilities",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        InteractiveMapCanvas(
                            userCoordinates = activeToDisplay.coordinates,
                            facilities = facilities,
                            isEmergency = activeToDisplay.status != EmergencyStatus.RESOLVED
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // AI Reasoning Engine Card (Prompt requirement: explain WHY the AI reached the conclusion)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GuardianNavyDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F2E4D)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = AzureGlow, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Why Nexora AI flagged this situation:",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AzureGlow
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                activeToDisplay.aiAssessment.reasoningPoints.forEach { pt ->
                                    Text(
                                        text = "• $pt",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // AI-Generated Incident Summary (Required by prompt)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GuardianNavyDark,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "AI-GENERATED RESPONDER BRIEF",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = SafeEmerald
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = activeToDisplay.aiAssessment.summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Intelligent Timeline
                        Text(
                            text = "Incident Timeline",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        TimelineView(events = activeToDisplay.timeline)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Responder Action Bar: [Contact User], [Notify Contacts], [Resolve]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { /* Demo phone call simulation */ },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = AzureGlow, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("CONTACT USER", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = AzureGlow)
                            }

                            if (activeToDisplay.status != EmergencyStatus.RESOLVED) {
                                Button(
                                    onClick = { onResolveIncident(activeToDisplay.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SafeEmerald),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("resolve_incident_button")
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("RESOLVE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
