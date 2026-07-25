package com.timeboundary.app.ui.screens.picker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.timeboundary.app.data.repository.InstalledAppItem
import com.timeboundary.app.ui.theme.MintPrimary
import com.timeboundary.app.ui.theme.SlateBorder
import com.timeboundary.app.ui.theme.SlateCard
import com.timeboundary.app.ui.theme.TextMuted
import com.timeboundary.app.ui.theme.TextPrimary
import com.timeboundary.app.ui.theme.TextSecondary

@Composable
fun AppPickerScreen(
    viewModel: AppPickerViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedAppForLimit by remember { mutableStateOf<InstalledAppItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Top App Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Select Monitored Apps",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Input
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Search installed apps...", color = TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextMuted
                )
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = TextMuted
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp)),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SlateCard,
                unfocusedContainerColor = SlateCard,
                focusedBorderColor = MintPrimary,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MintPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = uiState.filteredApps,
                    key = { it.packageName }
                ) { app ->
                    AppPickerRow(
                        app = app,
                        onRowClick = {
                            selectedAppForLimit = app
                        },
                        onCheckboxChange = { isChecked ->
                            if (isChecked) {
                                selectedAppForLimit = app
                            } else {
                                viewModel.removeAppLimit(app.packageName)
                            }
                        }
                    )
                }
            }
        }
    }

    // Duration Selector Dialog
    selectedAppForLimit?.let { app ->
        DurationPickerDialog(
            app = app,
            onDismiss = { selectedAppForLimit = null },
            onConfirm = { duration ->
                viewModel.setAppLimit(app.packageName, app.appLabel, duration)
                selectedAppForLimit = null
            }
        )
    }
}

@Composable
fun AppPickerRow(
    app: InstalledAppItem,
    onRowClick: () -> Unit,
    onCheckboxChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SlateCard)
            .border(
                1.dp,
                if (app.isMonitored) MintPrimary.copy(alpha = 0.5f) else SlateBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onRowClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (app.icon != null) {
                Image(
                    bitmap = app.icon.toBitmap(56, 56).asImageBitmap(),
                    contentDescription = app.appLabel,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MintPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.appLabel.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MintPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = app.appLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = if (app.isMonitored) "${app.durationMinutes} min limit configured" else "Tap to set time limit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (app.isMonitored) MintPrimary else TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        Checkbox(
            checked = app.isMonitored,
            onCheckedChange = onCheckboxChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MintPrimary,
                uncheckedColor = TextMuted,
                checkmarkColor = MaterialTheme.colorScheme.background
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DurationPickerDialog(
    app: InstalledAppItem,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val presets = listOf(5, 10, 15, 20, 30, 45, 60)
    var selectedDuration by remember { mutableIntStateOf(app.durationMinutes) }
    var customText by remember { mutableStateOf(if (presets.contains(app.durationMinutes)) "" else app.durationMinutes.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SlateCard,
        title = {
            Text(
                text = "Set Session Limit: ${app.appLabel}",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        },
        text = {
            Column {
                Text(
                    text = "Timer resets every single time you launch ${app.appLabel}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Preset Durations (Minutes)",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { minutes ->
                        val isSelected = (selectedDuration == minutes && customText.isBlank())
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedDuration = minutes
                                customText = ""
                            },
                            label = { Text("$minutes min") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MintPrimary,
                                selectedLabelColor = MaterialTheme.colorScheme.background,
                                containerColor = SlateBorder.copy(alpha = 0.5f),
                                labelColor = TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = customText,
                    onValueChange = { input ->
                        customText = input.filter { it.isDigit() }
                        input.toIntOrNull()?.let { minutes ->
                            if (minutes in 1..720) {
                                selectedDuration = minutes
                            }
                        }
                    },
                    label = { Text("Or enter custom minutes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MintPrimary,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedDuration)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MintPrimary,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text("Save Limit", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
