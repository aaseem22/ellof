package com.aaseem18.ellof.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WidgetUpdateScheduler {

    private const val WORK_NAME = "REMINDER_WIDGET_UPDATE"
    private const val TAG = "WidgetUpdateScheduler"

    /**
     * Schedule a widget update using WorkManager
     * This is good for background updates
     */
    fun enqueue(context: Context) {
        val work = OneTimeWorkRequestBuilder<ReminderWidgetWorker>()
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                work
            )

        Log.d(TAG, "Widget update enqueued via WorkManager")
    }

    /**
     * Update widget immediately without WorkManager
     * Use this for instant updates when user performs an action
     */
    fun updateNow(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(ReminderWidget::class.java)

                Log.d(TAG, "Updating ${glanceIds.size} widgets immediately")

                glanceIds.forEach { id ->
                    ReminderWidget().update(context, id)
                }

                Log.d(TAG, "Immediate widget update completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating widget immediately", e)
            }
        }
    }

    /**
     * Update widget both immediately and schedule a follow-up
     * Best approach for user-triggered actions
     */
    fun updateImmediatelyAndSchedule(context: Context) {
        updateNow(context)
        enqueue(context)
    }
}