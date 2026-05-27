package com.example.ui

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.DevotionApplication
import com.example.data.repository.ReadingRepository
import com.example.receiver.ReminderReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ReadingRepository = (application as DevotionApplication).repository

    val streakStats: StateFlow<ReadingRepository.StreakStats> = repository.getStreakStatsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ReadingRepository.StreakStats(
                currentStreak = 0,
                highestStreak = 0,
                totalCompleted = 0,
                readToday = false
            )
        )

    private val _fontSize = MutableStateFlow(20f)
    val fontSize: StateFlow<Float> = _fontSize.asStateFlow()

    fun updateFontSize(newSize: Float) {
        _fontSize.value = newSize
    }

    fun toggleTodayReading() {
        viewModelScope.launch {
            val stats = streakStats.value
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val todayStr = sdf.format(Date())

            if (stats.readToday) {
                repository.unmarkAsCompleted(todayStr)
            } else {
                repository.markAsCompleted(todayStr)
                // Dismiss the ongoing reminder notification safely
                val notificationManager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(ReminderReceiver.NOTIFICATION_ID)
            }
        }
    }
}
