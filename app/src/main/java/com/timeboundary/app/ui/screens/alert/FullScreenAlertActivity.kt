package com.timeboundary.app.ui.screens.alert

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassDisabled
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timeboundary.app.data.preferences.PreferenceManager
import com.timeboundary.app.service.AppMonitoringService
import com.timeboundary.app.ui.theme.MintPrimary
import com.timeboundary.app.ui.theme.RoseAccent
import com.timeboundary.app.ui.theme.SkySecondary
import com.timeboundary.app.ui.theme.SlateBorder
import com.timeboundary.app.ui.theme.SlateCard
import com.timeboundary.app.ui.theme.TimeBoundaryTheme
import com.timeboundary.app.ui.theme.TextMuted
import com.timeboundary.app.ui.theme.TextPrimary
import com.timeboundary.app.ui.theme.TextSecondary

class FullScreenAlertActivity : ComponentActivity() {

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(applicationContext)

        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        val appLabel = intent.getStringExtra(EXTRA_APP_LABEL) ?: "Monitored App"
        val durationMinutes = intent.getIntExtra(EXTRA_DURATION_MINUTES, 15)
        val sessionId = intent.getLongExtra(EXTRA_SESSION_ID, System.currentTimeMillis())

        val prefs = preferenceManager.preferencesFlow.value
        val currentSnoozes = preferenceManager.getSnoozeCountForSession(sessionId.toString())
        val canSnooze = (prefs.maxSnoozeCount > 0) && (currentSnoozes < prefs.maxSnoozeCount)

        setContent {
            TimeBoundaryTheme {
                FullScreenAlertScreen(
                    appLabel = appLabel,
                    durationMinutes = durationMinutes,
                    canSnooze = canSnooze,
                    currentSnoozes = currentSnoozes,
                    maxSnoozes = prefs.maxSnoozeCount,
                    onCloseApp = {
                        // Launch home screen intent to minimize app
                        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_HOME)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(homeIntent)
                        finish()
                    },
                    onSnooze = {
                        preferenceManager.incrementSnoozeCountForSession(sessionId.toString())
                        finish()
                    },
                    onDismiss = {
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_APP_LABEL = "extra_app_label"
        const val EXTRA_DURATION_MINUTES = "extra_duration_minutes"
        const val EXTRA_SESSION_ID = "extra_session_id"
    }
}

@Composable
fun FullScreenAlertScreen(
    appLabel: String,
    durationMinutes: Int,
    canSnooze: Boolean,
    currentSnoozes: Int,
    maxSnoozes: Int,
    onCloseApp: () -> Unit,
    onSnooze: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, RoseAccent, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hourglass Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(RoseAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassDisabled,
                        contentDescription = null,
                        tint = RoseAccent,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Time's Up!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = RoseAccent,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You've been on $appLabel for $durationMinutes minutes.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Time Boundary reached. Step back, take a breath, and focus on what matters.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = TextSecondary
                )

                if (maxSnoozes > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Snoozes used: $currentSnoozes of $maxSnoozes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Action 1: Close App (Primary CTA)
                Button(
                    onClick = onCloseApp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoseAccent,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Close $appLabel",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action 2: Snooze (+5 min) if enabled
                if (canSnooze) {
                    OutlinedButton(
                        onClick = onSnooze,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Snooze,
                                contentDescription = null,
                                tint = SkySecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+5 Min Snooze",
                                color = SkySecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Action 3: Dismiss
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Dismiss Reminder",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
