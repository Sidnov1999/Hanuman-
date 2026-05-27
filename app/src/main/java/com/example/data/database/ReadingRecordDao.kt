package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingRecordDao {
    @Query("SELECT * FROM reading_records ORDER BY dateString DESC")
    fun getAllRecordsFlow(): Flow<List<ReadingRecord>>

    @Query("SELECT * FROM reading_records WHERE dateString = :dateString LIMIT 1")
    suspend fun getRecordByDate(dateString: String): ReadingRecord?

    @Query("SELECT * FROM reading_records WHERE dateString = :dateString LIMIT 1")
    fun getRecordByDateFlow(dateString: String): Flow<ReadingRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ReadingRecord)

    @Query("DELETE FROM reading_records WHERE dateString = :dateString")
    suspend fun deleteRecord(dateString: String)
}
