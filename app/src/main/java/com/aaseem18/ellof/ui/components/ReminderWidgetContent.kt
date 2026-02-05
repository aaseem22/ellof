package com.aaseem18.ellof.ui.components

// Compose-like APIs used by Glance
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Glance core
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.currentState

// Glance actions
import androidx.glance.action.actionParametersOf
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle

// Theme colors
import com.aaseem18.ellof.ui.theme.NudeBackground
import com.aaseem18.ellof.ui.theme.TextPrimary

// Widget action
import com.aaseem18.ellof.widget.CompleteReminderAction
import com.aaseem18.ellof.widget.ReminderWidget

// Android resources
import com.aaseem18.ellof.R
import com.aaseem18.ellof.data.local.ReminderEntity
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReminderWidgetContent() {
    // Read reminders from widget state
    val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
    val remindersJson = prefs[ReminderWidget.REMINDERS_KEY] ?: "[]"

    val reminders = parseReminders(remindersJson)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(NudeBackground))
            .padding(12.dp)
    ) {
        // Header
        Text(
            text = "Today 🌼",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(TextPrimary)
            )
        )

        Spacer(GlanceModifier.height(8.dp))

        // Show reminders or empty state
        if (reminders.isEmpty()) {
            // Empty state
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "No reminders yet",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ColorProvider(TextPrimary)
                    )
                )
            }
        } else {
            // Show all reminders
            Column {
                reminders.forEach { reminder ->
                    ReminderRow(
                        id = reminder.id,
                        title = reminder.title,
                        triggerTime = reminder.triggerTime
                    )
                    Spacer(GlanceModifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun ReminderRow(
    id: Int,
    title: String,
    triggerTime: Long
) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ✅ Complete Button
        Image(
            provider = ImageProvider(R.drawable.check),
            contentDescription = "Mark done",
            modifier = GlanceModifier
                .size(24.dp)
                .clickable(
                    actionRunCallback<CompleteReminderAction>(
                        parameters = actionParametersOf(
                            ActionParameters.Key<Int>("id") to id
                        )
                    )
                )
        )

        Spacer(GlanceModifier.width(10.dp))

        Column(
            modifier = GlanceModifier.defaultWeight()
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = ColorProvider(TextPrimary),
                    fontWeight = FontWeight.Medium
                )
            )

            Text(
                text = formatTime(triggerTime),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = ColorProvider(TextPrimary)
                )
            )
        }
    }
}

// Helper function to parse JSON reminders
private fun parseReminders(json: String): List<ReminderEntity> {
    return try {
        if (json == "[]") return emptyList()

        val jsonArray = JSONArray(json)
        val reminders = mutableListOf<ReminderEntity>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            val reminder = ReminderEntity(
                id = jsonObject.getInt("id"),
                title = jsonObject.getString("title"),
                triggerTime = jsonObject.getLong("triggerTime"),
                isTimer = jsonObject.getBoolean("isTimer"),
                imageUri = jsonObject.getString("imageUri").takeIf { it.isNotEmpty() },
                isCompleted = jsonObject.getBoolean("isCompleted")
            )

            reminders.add(reminder)
        }

        reminders
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

// Helper function to format time
private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = timestamp - now

    return when {
        diff < 0 -> "Overdue"
        diff < 60 * 60 * 1000 -> { // Less than 1 hour
            val minutes = (diff / (60 * 1000)).toInt()
            "in $minutes min"
        }
        diff < 24 * 60 * 60 * 1000 -> { // Less than 24 hours
            val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
        else -> {
            val sdf = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}