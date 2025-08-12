package com.mangaverse.reader.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mangaverse.reader.core.data.database.entity.ReadingHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Data Access Object for reading history operations
 */
@Dao
interface ReadingHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(readingHistory: ReadingHistoryEntity)

    @Update
    suspend fun update(readingHistory: ReadingHistoryEntity)

    @Delete
    suspend fun delete(readingHistory: ReadingHistoryEntity)

    @Query("DELETE FROM reading_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM reading_history WHERE mangaId = :mangaId")
    suspend fun deleteByMangaId(mangaId: String)

    @Query("DELETE FROM reading_history")
    suspend fun deleteAll()

    @Query("SELECT * FROM reading_history WHERE id = :id")
    suspend fun getById(id: String): ReadingHistoryEntity?

    @Query("SELECT * FROM reading_history WHERE chapterId = :chapterId")
    suspend fun getChapterReadingHistory(chapterId: String): ReadingHistoryEntity?

    @Query("SELECT * FROM reading_history ORDER BY timestamp DESC LIMIT :limit")
    fun getReadingHistoryFlow(limit: Int): Flow<List<ReadingHistoryEntity>>

    @Query("SELECT * FROM reading_history WHERE mangaId = :mangaId ORDER BY timestamp DESC")
    fun getMangaReadingHistoryFlow(mangaId: String): Flow<List<ReadingHistoryEntity>>

    @Query("SELECT * FROM reading_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getReadingHistory(limit: Int): List<ReadingHistoryEntity>

    @Query("SELECT * FROM reading_history WHERE mangaId = :mangaId ORDER BY timestamp DESC")
    suspend fun getMangaReadingHistory(mangaId: String): List<ReadingHistoryEntity>

    @Query("""
        UPDATE reading_history 
        SET pageNumber = :pageNumber, timestamp = :timestamp, sessionDuration = :sessionDuration 
        WHERE chapterId = :chapterId
    """)
    suspend fun updateReadingHistory(
        chapterId: String,
        pageNumber: Int,
        timestamp: Date,
        sessionDuration: Long
    )

    @Query("""
        SELECT DISTINCT mangaId FROM reading_history 
        ORDER BY timestamp DESC LIMIT :limit
    """)
    suspend fun getRecentlyReadMangaIds(limit: Int): List<String>

    @Query("""
        SELECT COUNT(*) FROM reading_history 
        WHERE mangaId = :mangaId
    """)
    suspend fun getReadingHistoryCountForManga(mangaId: String): Int
}