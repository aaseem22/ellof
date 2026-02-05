package com.aaseem18.ellof.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.aaseem18.ellof.R
import com.aaseem18.ellof.notification.ReminderNotification

class ReminderAlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ReminderAlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "=== Alarm received! ===")

        val title = intent.getStringExtra("title") ?: "Reminder"
        val imageUri = intent.getStringExtra("imageUri")
        val reminderId = intent.getIntExtra("id", 0)

        Log.d(TAG, "Reminder ID: $reminderId")
        Log.d(TAG, "Title: $title")
        Log.d(TAG, "Image URI: $imageUri")

        try {
            val notification = NotificationCompat.Builder(
                context,
                ReminderNotification.CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            if (imageUri != null) {
                try {
                    val bitmap = BitmapFactory.decodeFile(imageUri)
                    if (bitmap != null) {
                        notification.setStyle(
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap)
                        )
                        Log.d(TAG, "Image loaded successfully")
                    } else {
                        Log.w(TAG, "Failed to decode image from URI: $imageUri")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading image", e)
                }
            }

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(reminderId, notification.build())

            Log.d(TAG, "✅ Notification posted successfully!")
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ SecurityException: Missing notification permission", e)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to show notification", e)
        }
    }
}