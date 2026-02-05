package com.aaseem18.ellof.widget


import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWidgetWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(ReminderWidget::class.java)

        glanceIds.forEach { id ->
            ReminderWidget().update(context, id)
        }

        return Result.success()
    }
}
