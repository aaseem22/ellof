package com.aaseem18.ellof.notification


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object ReminderNotification {

    const val CHANNEL_ID = "ellof_reminders"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Ellof Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Gentle reminders for Ellof"
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
