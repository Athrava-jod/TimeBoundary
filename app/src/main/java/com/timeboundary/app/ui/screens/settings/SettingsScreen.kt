package com.timeboundary.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeboundary.app.data.model.NotificationStyle
import com.timeboundary.app.ui.theme.MintPrimary
import com.timeboundary.app.ui.theme.RoseAccent
import com.timeboundary.app.ui.theme.SkySecondary
import com.timeboundary.app.ui.theme.SlateBorder
import com.timeboundary.app.ui.theme.SlateCard
import com.timeboundary.app.ui.theme.TextMuted
import com.timeboundary.app.ui.theme.TextPrimary
import com.timeboundary.app.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val prefs by viewModel.preferencesState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Configure reminder enforcement and session parameters",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 1. Global Monitoring Toggle Card
        SettingsSectionCard(
            title = "Global Monitoring",
            description = "Enable or pause background app usage monitoring across all configured apps.",
            icon = Icons.Default.PowerSettingsNew,
            color = MintPrimary
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (prefs.globalMonitoringEnabled) "Monitoring Enabled" else "Monitoring Paused",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                Switch(
                    checked = prefs.globalMonitoringEnabled,
                    onCheckedChange = { viewModel.setGlobalMonitoringEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.background,
                        checkedTrackColor = MintPrimary,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SlateBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Notification Enforcement Style
        SettingsSectionCard(
            title = "Notification Style",
            description = "Choose how TimeBoundary alerts you when a session limit expires.",
            icon = Icons.Default.NotificationsActive,
            color = SkySecondary
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val context = LocalContext.current
                StyleRadioButton(
                    title = "Gentle Mode",
                    description = "Delivers a high-priority notification with sound & vibration.",
                    isSelected = prefs.notificationStyle == NotificationStyle.GENTLE,
                    onSelect = { viewModel.setNotificationStyle(NotificationStyle.GENTLE) }
                )

                StyleRadioButton(
                    title = "Strict Mode",
                    description = "Triggers a full-screen overlay reminder over the active application.",
                    isSelected = prefs.notificationStyle == NotificationStyle.STRICT,
                    onSelect = {
                        if (com.timeboundary.app.utils.PermissionUtils.hasOverlayPermission(context)) {
                            viewModel.setNotificationStyle(NotificationStyle.STRICT)
                        } else {
                            com.timeboundary.app.utils.PermissionUtils.openOverlaySettings(context)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Snooze Configuration
        SettingsSectionCard(
            title = "Session Snooze Limit",
            description = "Allow adding '+5 minutes' to an active session, capped per session.",
            icon = Icons.Default.Snooze,
            color = SkySecondary
        ) {
            val snoozeOptions = listOf(0, 1, 2, 3, 5)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                snoozeOptions.forEach { count ->
                    val isSelected = (prefs.maxSnoozeCount == count)
                    val labelText = if (count == 0) "No Snoozing (0)" else "$count Time${if (count > 1) "s" else ""}"

                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setMaxSnoozeCount(count) },
                        label = { Text(labelText) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MintPrimary,
                            selectedLabelColor = MaterialTheme.colorScheme.background,
                            containerColor = SlateBorder.copy(alpha = 0.5f),
                            labelColor = TextPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Reset Data Card
        SettingsSectionCard(
            title = "Daily Statistics",
            description = "Reset today's open counts and limit exceeded logs.",
            icon = Icons.Default.DeleteForever,
            color = RoseAccent
        ) {
            OutlinedButton(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Reset All Session Stats",
                    color = RoseAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. About Card
        SettingsSectionCard(
            title = "About TimeBoundary",
            description = "TimeBoundary v1.0.0 • Native Android Kotlin\nBuilt for mindful digital wellbeing with per-session limits.",
            icon = Icons.Default.Info,
            color = TextMuted
        ) { }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = SlateCard,
            title = {
                Text("Reset Statistics?", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "This will reset all recorded app opens and limit exceeded counts for today. Monitored app configurations will remain intact.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllStats()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseAccent)
                ) {
                    Text("Reset Stats", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
fun StyleRadioButton(
    title: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MintPrimary.copy(alpha = 0.1f) else SlateBorder.copy(alpha = 0.2f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(
                selectedColor = MintPrimary,
                unselectedColor = TextMuted
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontSize = 15.sp
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
