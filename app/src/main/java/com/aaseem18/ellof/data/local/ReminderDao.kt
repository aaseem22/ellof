package com.aaseem18.ellof.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders ORDER BY triggerTime ASC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: Int): ReminderEntity?

    @Query("SELECT * FROM reminders")
    suspend fun getAllRemindersOnce(): List<ReminderEntity>

    @Query("UPDATE reminders SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: Int)

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY triggerTime ASC")
    fun getActiveReminders(): Flow<List<ReminderEntity>>


}