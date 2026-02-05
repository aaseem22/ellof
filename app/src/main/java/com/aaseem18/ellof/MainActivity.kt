package com.aaseem18.ellof

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.aaseem18.ellof.data.local.ReminderDatabase
import com.aaseem18.ellof.navigation.EllofNavHost
import com.aaseem18.ellof.notification.ReminderNotification
import com.aaseem18.ellof.ui.theme.EllofTheme
import com.aaseem18.ellof.viewmodel.ReminderViewModel
import com.aaseem18.ellof.viewmodel.ReminderViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 📢 Create notification channel FIRST
        ReminderNotification.createChannel(this)

        // 📢 Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        // 🔗 Create ViewModel properly
        val dao = ReminderDatabase.getDatabase(this).reminderDao()
        val factory = ReminderViewModelFactory(dao)
        val viewModel = ViewModelProvider(this, factory)[ReminderViewModel::class.java]

        setContent {
            EllofTheme {
                // Check for exact alarm permission at startup
                var showPermissionDialog by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        if (!alarmManager.canScheduleExactAlarms()) {
                            showPermissionDialog = true
                        }
                    }
                }

                if (showPermissionDialog) {
                    AlarmPermissionDialog(
                        onDismiss = { showPermissionDialog = false },
                        onOpenSettings = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                startActivity(intent)
                            }
                            showPermissionDialog = false
                        }
                    )
                }

                EllofNavHost(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Optionally check again when app resumes (in case user granted permission)
    }
}

@Composable
fun AlarmPermissionDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Permission Required",
                color = com.aaseem18.ellof.ui.theme.TextPrimary
            )
        },
        text = {
            Text(
                "This app needs permission to schedule exact alarms for your reminders. Please enable it in settings.",
                color = com.aaseem18.ellof.ui.theme.TextSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text(
                    "Open Settings",
                    color = com.aaseem18.ellof.ui.theme.MustardYellow
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Later",
                    color = com.aaseem18.ellof.ui.theme.TextSecondary
                )
            }
        },
        containerColor = com.aaseem18.ellof.ui.theme.NudeSurface,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    )
}