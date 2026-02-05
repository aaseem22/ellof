package com.aaseem18.ellof.viewmodel


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaseem18.ellof.data.local.ReminderDao
import com.aaseem18.ellof.data.local.ReminderEntity
import com.aaseem18.ellof.notification.AlarmScheduler
import com.aaseem18.ellof.widget.WidgetUpdateScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val reminderDao: ReminderDao
) : ViewModel() {

    companion object {
        private const val TAG = "ReminderViewModel"
    }

    val reminders: StateFlow<List<ReminderEntity>> =
        reminderDao.getAllReminders()
            .onEach { list ->
                Log.d(TAG, "Reminders updated: ${list.size}")
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun addReminder(
        title: String,
        triggerTime: Long,
        isTimer: Boolean,
        imageUri: String?,
        context: Context, // ✅ ADD CONTEXT PARAMETER
        onReminderSaved: (ReminderEntity) -> Unit,
    ) {
        viewModelScope.launch {
            val reminder = ReminderEntity(
                title = title,
                triggerTime = triggerTime,
                isTimer = isTimer,
                imageUri = imageUri
            )

            // Insert and get the generated ID
            val insertedId = reminderDao.insertReminder(reminder)

            // Create a new reminder object with the correct ID
            val savedReminder = reminder.copy(id = insertedId.toInt())

            Log.d(TAG, "Reminder saved with ID: ${savedReminder.id}")
            onReminderSaved(savedReminder)

            // ✅ UPDATE WIDGET IMMEDIATELY
            WidgetUpdateScheduler.updateImmediatelyAndSchedule(context)
            Log.d(TAG, "Widget update triggered immediately")
        }
    }

    fun deleteReminder(reminder: ReminderEntity, context: Context) {
        viewModelScope.launch {
            reminderDao.deleteReminder(reminder)

            AlarmScheduler.cancel(context, reminder.id)

            Log.d("ReminderVM", "Deleted & alarm cancelled: ${reminder.title}")

            WidgetUpdateScheduler.updateImmediatelyAndSchedule(context)
        }
    }

    fun completeReminder(reminder: ReminderEntity, context: Context) {
        viewModelScope.launch {
            reminderDao.markCompleted(reminder.id)
            AlarmScheduler.cancel(context, reminder.id)

            Log.d("ReminderVM", "Completed: ${reminder.title}")

            // ✅ UPDATE WIDGET IMMEDIATELY
            WidgetUpdateScheduler.updateImmediatelyAndSchedule(context)
        }
    }

    fun markCompleted(reminder: ReminderEntity, context: Context) {
        viewModelScope.launch {
            val updated = reminder.copy(isCompleted = true)

            reminderDao.updateReminder(updated)

            // 🔕 Cancel alarm to avoid ghost notification
            AlarmScheduler.cancel(context, reminder.id)

            Log.d("ReminderVM", "Reminder completed: ${reminder.title}")

            WidgetUpdateScheduler.updateImmediatelyAndSchedule(context)
        }
    }
}