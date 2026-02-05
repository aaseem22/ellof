package com.aaseem18.ellof.data.model

/**
 * Domain model for a reminder
 * Used by UI & ViewModel
 */
data class Reminder(
    val id: Int = 0,
    val title: String,
    val triggerTime: Long,   // Epoch millis
    val isTimer: Boolean,
    val imageUri: String? = null
)
