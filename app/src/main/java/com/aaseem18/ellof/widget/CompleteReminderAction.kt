package com.aaseem18.ellof.widget

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.aaseem18.ellof.data.local.ReminderDatabase
import com.aaseem18.ellof.notification.AlarmScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class CompleteReminderAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val reminderId = parameters[ActionParameters.Key<Int>("id")] ?: return

        Log.d("CompleteReminderAction", "Completing reminder with ID: $reminderId")

        val db = ReminderDatabase.getDatabase(context)
        val dao = db.reminderDao()

        val reminder = dao.getReminderById(reminderId)
        if (reminder == null) {
            Log.e("CompleteReminderAction", "Reminder not found: $reminderId")
            return
        }

        // 1️⃣ Mark completed in database
        dao.markCompleted(reminderId)
        Log.d("CompleteReminderAction", "Marked as completed: ${reminder.title}")

        // 2️⃣ Cancel alarm
        AlarmScheduler.cancel(context, reminderId)
        Log.d("CompleteReminderAction", "Cancelled alarm for: ${reminder.title}")

        // 3️⃣ Update widget state with fresh data from database
        val updatedReminders = withContext(Dispatchers.IO) {
            dao.getAllRemindersOnce()
                .filter { !it.isCompleted }
                .sortedBy { it.triggerTime }
                .take(5)
        }

        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[ReminderWidget.REMINDERS_KEY] = remindersToJson(updatedReminders)
            }
        }

        // 4️⃣ Refresh the widget UI
        ReminderWidget().update(context, glanceId)

        Log.d("CompleteReminderAction", "Widget updated successfully")
    }

    private fun remindersToJson(reminders: List<com.aaseem18.ellof.data.local.ReminderEntity>): String {
        val jsonArray = JSONArray()
        reminders.forEach { reminder ->
            val jsonObject = JSONObject().apply {
                put("id", reminder.id)
                put("title", reminder.title)
                put("triggerTime", reminder.triggerTime)
                put("isTimer", reminder.isTimer)
                put("imageUri", reminder.imageUri ?: "")
                put("isCompleted", reminder.isCompleted)
            }
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }
}