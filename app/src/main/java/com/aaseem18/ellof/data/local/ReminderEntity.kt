package com.aaseem18.ellof.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val triggerTime: Long,
    val isTimer: Boolean,
    val imageUri: String?,
    val isCompleted: Boolean = false // ✅ NEW
)

