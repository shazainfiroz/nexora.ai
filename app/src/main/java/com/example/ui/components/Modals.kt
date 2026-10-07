package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MedicalProfile
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AzureElectric
import com.example.ui.theme.AzureGlow
import com.example.ui.theme.EmergencyCrimson
import com.example.ui.theme.GuardianNavyCard
import com.example.ui.theme.GuardianNavyDark
import com.example.ui.theme.SafeEmerald

@Composable
fun AiTransparencyDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GuardianNavyCard,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = AzureGlow, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Safety & Transparency",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AlertOrange.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AlertOrange)
                ) {
                    Text(
                        text = "“AI confidence is a probabilistic estimate, not an infallible guarantee. Always verify emergency conditions when possible.”",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = AlertOrange,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                SectionCard(
                    title = "1. Data Used by AI Engine",
                    desc = "• Tri-axial accelerometer (G-force vector analysis)\n• Gyroscopic angular rotational velocity\n• Post-impact stationary inactivity window\n• Acoustic decibel levels & stress formant classification\n• Coarse or fine geographic spatial coordinates"
                )

                SectionCard(
                    title = "2. Calibrated Confidence Scoring",
                    desc = "Confidence is generated through Bayesian multi-signal fusion. Single spikes (e.g. dropping phone on a soft bed) receive low confidence (<35%) and do not escalate unless unresponsiveness is maintained."
                )

                SectionCard(
                    title = "3. Human-in-the-Loop Supremacy",
                    desc = "Human input ALWAYS supersedes machine inferences. Pressing 'I AM SAFE' cancels escalation instantly and archives the event."
                )

                SectionCard(
                    title = "4. Zero-Trust Privacy Model",
                    desc = "Audio streams are processed strictly on-device using local DSP keywords ('Help', 'Emergency'). No ambient audio is transmitted or stored on cloud servers without explicit user distress verification."
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AzureElectric)
            ) {
                Text("UNDERSTOOD", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun MedicalProfileDialog(
    profile: MedicalProfile,
    onSave: (MedicalProfile) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(profile.fullName) }
    var ageStr by remember { mutableStateOf(profile.age.toString()) }
    var bloodType by remember { mutableStateOf(profile.bloodType) }
    var allergies by remember { mutableStateOf(profile.allergies) }
    var medications by remember { mutableStateOf(profile.medications) }
    var notes by remember { mutableStateOf(profile.emergencyNotes) }
    var organDonor by remember { mutableStateOf(profile.organDonor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GuardianNavyCard,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.MedicalServices, contentDescription = null, tint = EmergencyCrimson, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Emergency Medical ID",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { ageStr = it },
                        label = { Text("Age") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = bloodType,
                        onValueChange = { bloodType = it },
                        label = { Text("Blood Type") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = allergies,
                    onValueChange = { allergies = it },
                    label = { Text("Allergies") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = medications,
                    onValueChange = { medications = it },
                    label = { Text("Medications") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Emergency Notes") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Checkbox(
                        checked = organDonor,
                        onCheckedChange = { organDonor = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Designated Organ Donor", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ageVal = ageStr.toIntOrNull() ?: profile.age
                    onSave(profile.copy(
                        fullName = name,
                        age = ageVal,
                        bloodType = bloodType,
                        allergies = allergies,
                        medications = medications,
                        emergencyNotes = notes,
                        organDonor = organDonor
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = SafeEmerald)
            ) {
                Text("SAVE PROFILE", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
fun AddContactDialog(
    onAdd: (name: String, relationship: String, phone: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Family") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GuardianNavyCard,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, tint = AzureGlow, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Emergency Contact",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Contact Name") },
                    placeholder = { Text("e.g. Sarah Connor") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = relationship,
                    onValueChange = { relationship = it },
                    label = { Text("Relationship") },
                    placeholder = { Text("e.g. Sister / Campus Security") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("+1 (555) 019-2834") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onAdd(name, relationship, phone)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AzureElectric)
            ) {
                Text("ADD CONTACT", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
private fun SectionCard(title: String, desc: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = GuardianNavyDark,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = AzureGlow)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
