package com.aaseem18.ellof.notification


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.aaseem18.ellof.data.local.ReminderDatabase
import com.aaseem18.ellof.widget.WidgetUpdateScheduler
import kotlinx.coroutines.*

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device rebooted, rescheduling alarms")

            val appContext = context.applicationContext

            CoroutineScope(Dispatchers.IO).launch {
                val dao = ReminderDatabase.getDatabase(appContext).reminderDao()
                val reminders = dao.getAllRemindersOnce()

                reminders.forEach { reminder ->
                    if (reminder.triggerTime > System.currentTimeMillis()) {
                        AlarmScheduler.schedule(appContext, reminder)
                        WidgetUpdateScheduler.enqueue(context)
                        Log.d(
                            "BootReceiver",
                            "Rescheduled: ${reminder.title}"
                        )
                    }
                }
            }
        }
    }
}
