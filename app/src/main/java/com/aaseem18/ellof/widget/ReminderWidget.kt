package com.aaseem18.ellof.widget

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.aaseem18.ellof.data.local.ReminderDatabase
import com.aaseem18.ellof.ui.components.ReminderWidgetContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class ReminderWidget : GlanceAppWidget() {

    companion object {
        val REMINDERS_KEY = stringPreferencesKey("reminders")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Fetch reminders from database and update widget state
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
            val reminders = fetchRemindersFromDb(context)
            val remindersJson = remindersToJson(reminders)

            prefs.toMutablePreferences().apply {
                this[REMINDERS_KEY] = remindersJson
            }
        }

        provideContent {
            ReminderWidgetContent()
        }
    }

    private suspend fun fetchRemindersFromDb(context: Context) = withContext(Dispatchers.IO) {
        val dao = ReminderDatabase.getDatabase(context).reminderDao()
        dao.getAllRemindersOnce()
            .filter { !it.isCompleted } // Only show active reminders
            .sortedBy { it.triggerTime }
            .take(5) // Limit to 5 reminders for widget
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