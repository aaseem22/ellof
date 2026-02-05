package com.aaseem18.ellof.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.aaseem18.ellof.alarm.ReminderAlarmReceiver
import com.aaseem18.ellof.data.local.ReminderEntity

object AlarmScheduler {

    private const val TAG = "AlarmScheduler"

    fun schedule(
        context: Context,
        reminder: ReminderEntity
    ) {
        Log.d(TAG, "=== Scheduling alarm ===")
        Log.d(TAG, "Reminder ID: ${reminder.id}")
        Log.d(TAG, "Title: ${reminder.title}")
        Log.d(TAG, "Trigger time: ${reminder.triggerTime}")
        Log.d(TAG, "Image URI: ${reminder.imageUri}")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            putExtra("title", reminder.title)
            putExtra("imageUri", reminder.imageUri)
            putExtra("id", reminder.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d(TAG, "PendingIntent created with ID: ${reminder.id}")

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.triggerTime,
                pendingIntent
            )
            Log.d(TAG, "✅ Alarm set successfully!")

            // Calculate time until alarm
            val timeUntil = reminder.triggerTime - System.currentTimeMillis()
            val minutesUntil = timeUntil / 1000 / 60
            Log.d(TAG, "Alarm will fire in $minutesUntil minutes")
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ SecurityException: Cannot schedule exact alarm", e)
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to schedule alarm", e)
            throw e
        }
    }

    fun cancel(context: Context, reminderId: Int) {
        Log.d(TAG, "Cancelling alarm for reminder ID: $reminderId")

        val intent = Intent(context, ReminderAlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        Log.d(TAG, "Alarm cancelled")
    }
}