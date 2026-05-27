package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_records")
data class ReadingRecord(
    @PrimaryKey
    val dateString: String, // Format: "yyyy-MM-dd"
    val completed: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
