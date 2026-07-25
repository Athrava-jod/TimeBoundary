package com.timeboundary.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timeboundary.app.ui.theme.MintPrimary
import com.timeboundary.app.ui.theme.RoseAccent
import com.timeboundary.app.ui.theme.SkySecondary
import com.timeboundary.app.ui.theme.SlateBorder
import com.timeboundary.app.ui.theme.SlateCard
import com.timeboundary.app.ui.theme.TextMuted
import com.timeboundary.app.ui.theme.TextPrimary
import com.timeboundary.app.ui.theme.TextSecondary

@Composable
fun PermissionOnboardingScreen(
    hasUsageAccess: Boolean,
    hasNotificationPermission: Boolean,
    hasExactAlarmPermission: Boolean,
    onGrantUsageAccessClick: () -> Unit,
    onGrantNotificationClick: () -> Unit,
    onGrantAlarmClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // App Shield Badge
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MintPrimary.copy(alpha = 0.15f))
                    .border(2.dp, MintPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MintPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to TimeBoundary",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Build healthy phone habits with mindful per-session countdown reminders.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Permission Card 1: Usage Access (Core Requirement)
            PermissionStatusCard(
                title = "Usage Access (Required)",
                description = "Allows TimeBoundary to detect when you open a monitored application to start your session timer.",
                icon = Icons.Default.QueryStats,
                isGranted = hasUsageAccess,
                onGrantClick = onGrantUsageAccessClick,
                isRequired = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Permission Card 2: Notifications
            PermissionStatusCard(
                title = "Notifications",
                description = "Delivers high-priority reminders when your configured session limit expires.",
                icon = Icons.Default.Notifications,
                isGranted = hasNotificationPermission,
                onGrantClick = onGrantNotificationClick,
                isRequired = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Permission Card 3: Exact Alarms
            PermissionStatusCard(
                title = "Exact Alarm Timing",
                description = "Ensures session timers fire precisely even if your phone is in battery saver mode.",
                icon = Icons.Default.Alarm,
                isGranted = hasExactAlarmPermission,
                onGrantClick = onGrantAlarmClick,
                isRequired = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy Promise
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateCard)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = SkySecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "100% Offline & Private: Your usage data never leaves your device. No cloud sync, no tracking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Main CTA Button
        Button(
            onClick = {
                if (hasUsageAccess) {
                    onContinueClick()
                } else {
                    onGrantUsageAccessClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MintPrimary,
                contentColor = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (hasUsageAccess) "Get Started" else "Grant Usage Access to Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PermissionStatusCard(
    title: String,
    description: String,
    icon: ImageVector,
    isGranted: Boolean,
    onGrantClick: () -> Unit,
    isRequired: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isGranted) MintPrimary.copy(alpha = 0.4f) else SlateBorder,
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isGranted) MintPrimary.copy(alpha = 0.2f) else TextMuted.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isGranted) MintPrimary else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }

                if (isGranted) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MintPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onGrantClick,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Grant",
                            color = if (isRequired) RoseAccent else SkySecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
