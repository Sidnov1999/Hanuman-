package com.example.data.repository

import com.example.data.database.ReadingRecord
import com.example.data.database.ReadingRecordDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReadingRepository(private val dao: ReadingRecordDao) {

    val allRecords: Flow<List<ReadingRecord>> = dao.getAllRecordsFlow()

    fun getRecordByDateFlow(dateString: String): Flow<ReadingRecord?> =
        dao.getRecordByDateFlow(dateString)

    suspend fun markAsCompleted(dateString: String) {
        val record = ReadingRecord(
            dateString = dateString,
            completed = true,
            timestamp = System.currentTimeMillis()
        )
        dao.insertRecord(record)
    }

    suspend fun unmarkAsCompleted(dateString: String) {
        dao.deleteRecord(dateString)
    }

    suspend fun getRecordByDate(dateString: String): ReadingRecord? =
        dao.getRecordByDate(dateString)

    // Struct to hold computed streak stats
    data class StreakStats(
        val currentStreak: Int,
        val highestStreak: Int,
        val totalCompleted: Int,
        val readToday: Boolean
    )

    fun getStreakStatsFlow(): Flow<StreakStats> {
        return allRecords.map { records ->
            val completedDatesSet = records.filter { it.completed }.map { it.dateString }.toSet()
            
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val todayStr = sdf.format(Date())
            
            val hasReadToday = todayStr in completedDatesSet

            // Calculate current streak
            val currentStreak = calculateCurrentStreak(completedDatesSet, todayStr, sdf)

            // Calculate highest streak
            val epochDays = completedDatesSet.map { dateStr ->
                getEpochDay(dateStr, sdf)
            }.sorted()
            val highestStreak = calculateHighestStreak(epochDays)

            StreakStats(
                currentStreak = currentStreak,
                highestStreak = highestStreak,
                totalCompleted = completedDatesSet.size,
                readToday = hasReadToday
            )
        }
    }

    private fun calculateCurrentStreak(
        completedDates: Set<String>,
        todayStr: String,
        sdf: SimpleDateFormat
    ): Int {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = sdf.format(cal.time)

        if (todayStr !in completedDates && yesterdayStr !in completedDates) {
            return 0
        }

        var streak = 0
        val checkCal = Calendar.getInstance()
        
        // If not completed today, start counting back from yesterday
        if (todayStr !in completedDates) {
            checkCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            val checkStr = sdf.format(checkCal.time)
            if (checkStr in completedDates) {
                streak++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    private fun calculateHighestStreak(epochDays: List<Long>): Int {
        if (epochDays.isEmpty()) return 0
        var maxStreak = 1
        var currentStreak = 1
        for (i in 1 until epochDays.size) {
            if (epochDays[i] == epochDays[i - 1] + 1) {
                currentStreak++
            } else if (epochDays[i] != epochDays[i - 1]) {
                currentStreak = 1
            }
            if (currentStreak > maxStreak) {
                maxStreak = currentStreak
            }
        }
        return maxStreak
    }

    private fun getEpochDay(dateString: String, sdf: SimpleDateFormat): Long {
        return try {
            val date = sdf.parse(dateString) ?: return 0L
            date.time / (1000 * 60 * 60 * 24)
        } catch (e: Exception) {
            0L
        }
    }
}
