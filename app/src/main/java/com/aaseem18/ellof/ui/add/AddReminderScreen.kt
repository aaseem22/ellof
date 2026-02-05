package com.aaseem18.ellof.ui.add

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aaseem18.ellof.ui.components.ImagePickerBox
import com.aaseem18.ellof.ui.components.PrimaryButton
import com.aaseem18.ellof.ui.theme.*
import com.aaseem18.ellof.viewmodel.ReminderViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.aaseem18.ellof.ui.components.CustomDatePickerDialog
import com.aaseem18.ellof.ui.components.CustomTimePickerDialog
import com.aaseem18.ellof.ui.components.MinutePickerDialog
import androidx.compose.ui.platform.LocalContext
import com.aaseem18.ellof.notification.AlarmScheduler
import androidx.compose.ui.draw.clip
import com.aaseem18.ellof.widget.WidgetUpdateScheduler

private const val TAG = "AddReminderScreen"

@Composable
fun AddReminderScreen(
    viewModel: ReminderViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var isTimer by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    var selectedTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val triggerTime: Long? = remember(selectedDate, selectedTime) {
        if (selectedDate != null && selectedTime != null) {
            selectedDate!!.apply {
                set(Calendar.HOUR_OF_DAY, selectedTime!!.first)
                set(Calendar.MINUTE, selectedTime!!.second)
                set(Calendar.SECOND, 0)
            }.timeInMillis
        } else null
    }

    var timerMinutes by remember { mutableStateOf<Int?>(null) }
    var showMinutePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var showPermissionWarning by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri?.toString()
        Log.d(TAG, "Image selected: $imageUri")
    }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NudeBackground)
            .padding(16.dp) // Consistent: outer padding
    ) {
        Spacer(modifier = Modifier.height(22.dp))

        // 🔙 Back
//        IconButton(
//            onClick = {
//                Log.d(TAG, "Back button clicked")
//                onBack()
//            },
//            modifier = Modifier.size(48.dp) // Consistent touch target
//        ) {
//            Icon(
//                imageVector = Icons.Default.ArrowBack,
//                contentDescription = "Back",
//                tint = TextPrimary
//            )
//        }

        Spacer(modifier = Modifier.height(16.dp)) // Consistent spacing

        // 📝 Title Input
        Text(
            text = "Reminder",
            color = TextPrimary,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(8.dp)) // Consistent spacing

        BasicTextField(
            value = title,
            onValueChange = {
                title = it
                Log.d(TAG, "Title changed: $it")
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)) // Softer corners
                .background(NudeSurface)
                .padding(16.dp), // Consistent padding
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
            decorationBox = { innerTextField ->
                if (title.isEmpty()) {
                    Text(
                        text = "What do you want to remember?",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(24.dp)) // Consistent section spacing

        // 📅 Date Picker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)) // Softer corners
                .background(NudeSurface)
                .clickable {
                    showDatePicker = true
                    Log.d(TAG, "Date picker opened")
                }
                .padding(16.dp), // Consistent padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate?.let {
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it.time)
                } ?: "Pick date",
                color = if (selectedDate == null) TextSecondary else TextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(12.dp)) // Consistent spacing

        // ⏰ Time Picker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)) // Softer corners
                .background(NudeSurface)
                .clickable {
                    showTimePicker = true
                    Log.d(TAG, "Time picker opened")
                }
                .padding(16.dp), // Consistent padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedTime?.let {
                    val (h, m) = it
                    String.format("%02d:%02d", h, m)
                } ?: "Pick time",
                color = if (selectedTime == null) TextSecondary else TextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp)) // Consistent section spacing

        // Subtle divider before timer section
        HorizontalDivider(
            thickness = 1.dp,
            color = Divider.copy(alpha = 0.3f),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // ⏳ Timer Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp), // Consistent padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Use Timer",
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isTimer,
                onCheckedChange = {
                    isTimer = it
                    Log.d(TAG, "Timer toggled: $it")
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MustardYellow, // Sparing yellow: only when active
                    checkedTrackColor = MustardYellow.copy(alpha = 0.4f),
                    uncheckedThumbColor = Divider,
                    uncheckedTrackColor = Divider.copy(alpha = 0.3f)
                )
            )
        }

        if (isTimer) {
            Spacer(modifier = Modifier.height(16.dp)) // Consistent spacing

            Text(
                text = "Timer (minutes)",
                color = TextPrimary,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp)) // Consistent spacing

            // iOS-style Minute Picker Trigger
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)) // Softer corners
                    .background(NudeSurface)
                    .clickable {
                        showMinutePicker = true
                        Log.d(TAG, "Minute picker opened")
                    }
                    .padding(16.dp), // Consistent padding
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timerMinutes?.let { "$it minutes" } ?: "Select minutes",
                    color = if (timerMinutes == null) TextSecondary else TextPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp)) // Consistent section spacing

        // Subtle divider before image section
        HorizontalDivider(
            thickness = 1.dp,
            color = Divider.copy(alpha = 0.3f),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 🖼️ Image Picker
        ImagePickerBox(
            text = if (imageUri == null) "Add image (optional)" else "Change image",
            onClick = { imagePicker.launch("image/*") }
        )

        Spacer(modifier = Modifier.weight(1f))

        // 💾 Save Button
        PrimaryButton(
            text = if (isSaving) "Saving..." else "Save Reminder",
            onClick = {
                Log.d(TAG, "=== Save button clicked ===")
                Log.d(TAG, "Title: $title")
                Log.d(TAG, "IsTimer: $isTimer")
                Log.d(TAG, "TimerMinutes: $timerMinutes")
                Log.d(TAG, "TriggerTime: $triggerTime")

                // Prevent double-clicks
                if (isSaving) {
                    Log.d(TAG, "Already saving, ignoring click")
                    return@PrimaryButton
                }

                isSaving = true

                val finalTriggerTime = if (isTimer) {
                    val mins = timerMinutes
                    if (mins == null) {
                        Log.e(TAG, "Timer minutes not set!")
                        isSaving = false
                        return@PrimaryButton
                    }
                    buildTimerTriggerTime(mins)
                } else {
                    val time = triggerTime
                    if (time == null) {
                        Log.e(TAG, "Trigger time not set!")
                        isSaving = false
                        return@PrimaryButton
                    }
                    time
                }

                Log.d(TAG, "Final trigger time: $finalTriggerTime")
                Log.d(TAG, "Formatted: ${formatDateTime(finalTriggerTime)}")

                // ⚠️ Warn user if permission not granted (but still save the reminder)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Log.w(TAG, "Exact alarm permission not granted, showing warning")
                        showPermissionWarning = true
                    }
                }

                Log.d(TAG, "Saving reminder...")


                viewModel.addReminder(
                    title = title,
                    triggerTime = finalTriggerTime,
                    isTimer = isTimer,
                    imageUri = imageUri,
                    context = context, // ✅ ADD THIS
                    onReminderSaved =
                 { reminder ->

                    Log.d(TAG, "=== Reminder saved callback ===")
                    Log.d(TAG, "Reminder ID: ${reminder.id}")
                    Log.d(TAG, "Reminder Title: ${reminder.title}")
                    Log.d(TAG, "Reminder TriggerTime: ${reminder.triggerTime}")

                    try {
                        Log.d(TAG, "Scheduling alarm...")
                        AlarmScheduler.schedule(context, reminder)
                        Log.d(TAG, "✅ Alarm scheduled successfully!")
                    } catch (e: SecurityException) {
                        Log.e(TAG, "❌ SecurityException scheduling alarm", e)
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Failed to schedule alarm", e)
                    }
                    WidgetUpdateScheduler.enqueue(context)
                    onBack()
                }
                )
            },
            enabled = !isSaving && if (isTimer) {
                title.isNotBlank() && timerMinutes != null
            } else {
                title.isNotBlank() && triggerTime != null
            }
        )
    }

    // Custom Date Picker Dialog
    if (showDatePicker) {
        CustomDatePickerDialog(
            initialDate = selectedDate ?: Calendar.getInstance(),
            onDismiss = {
                showDatePicker = false
                Log.d(TAG, "Date picker dismissed")
            },
            onConfirm = { date ->
                selectedDate = date
                showDatePicker = false
                Log.d(TAG, "Date selected: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date.time)}")
            }
        )
    }

    // Custom Time Picker Dialog
    if (showTimePicker) {
        CustomTimePickerDialog(
            initialTime = selectedTime ?: Pair(
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE)
            ),
            onDismiss = {
                showTimePicker = false
                Log.d(TAG, "Time picker dismissed")
            },
            onConfirm = { hour, minute ->
                selectedTime = Pair(hour, minute)
                showTimePicker = false
                Log.d(TAG, "Time selected: ${String.format("%02d:%02d", hour, minute)}")
            }
        )
    }

    // iOS-style Minute Picker Dialog
    if (showMinutePicker) {
        MinutePickerDialog(
            initialMinutes = timerMinutes ?: 5,
            onDismiss = {
                showMinutePicker = false
                Log.d(TAG, "Minute picker dismissed")
            },
            onConfirm = { selectedMinutes ->
                timerMinutes = selectedMinutes
                showMinutePicker = false
                Log.d(TAG, "Minutes selected: $selectedMinutes")
            }
        )
    }

    // Permission warning dialog (non-blocking)
    if (showPermissionWarning) {
        AlertDialog(
            onDismissRequest = { showPermissionWarning = false },
            title = {
                Text(
                    "Reminder Saved",
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    "Your reminder was saved, but alarms may not work properly. Please enable exact alarm permission in settings.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            context.startActivity(intent)
                        }
                        showPermissionWarning = false
                    }
                ) {
                    Text(
                        "Open Settings",
                        color = MustardYellow
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionWarning = false }) {
                    Text(
                        "OK",
                        color = TextSecondary
                    )
                }
            },
            containerColor = NudeSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

fun buildTimerTriggerTime(minutes: Int): Long {
    val now = System.currentTimeMillis()
    val triggerTime = now + (minutes * 60 * 1000L)
    Log.d(TAG, "Timer: $minutes minutes from now = ${formatDateTime(triggerTime)}")
    return triggerTime
}

fun formatDateTime(timeMillis: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timeMillis))
}