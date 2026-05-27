package com.example

import android.app.Application
import com.example.data.database.AppDatabase
import com.example.data.repository.ReadingRepository
import com.example.util.ReminderScheduler

class DevotionApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ReadingRepository(database.readingRecordDao()) }

    override fun onCreate() {
        super.onCreate()
        // Schedule 7:00 PM alarm reminder
        ReminderScheduler.scheduleDailyReminder(this)
    }
}
