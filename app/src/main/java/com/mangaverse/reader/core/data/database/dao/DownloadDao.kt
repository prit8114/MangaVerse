package com.mangaverse.reader.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mangaverse.reader.core.data.database.entity.DownloadEntity
import com.mangaverse.reader.core.data.database.entity.DownloadStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for download operations
 */
@Dao
interface DownloadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(download: DownloadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(downloads: List<DownloadEntity>)

    @Update
    suspend fun update(download: DownloadEntity)

    @Delete
    suspend fun delete(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE id = :downloadId")
    suspend fun deleteById(downloadId: String)

    @Query("DELETE FROM downloads WHERE chapterId = :chapterId")
    suspend fun deleteByChapterId(chapterId: String)

    @Query("DELETE FROM downloads WHERE mangaId = :mangaId")
    suspend fun deleteByMangaId(mangaId: String)

    @Query("SELECT * FROM downloads WHERE id = :downloadId")
    suspend fun getDownloadById(downloadId: String): DownloadEntity?

    @Query("SELECT * FROM downloads WHERE chapterId = :chapterId")
    suspend fun getDownloadByChapterId(chapterId: String): DownloadEntity?

    @Query("SELECT * FROM downloads ORDER BY queuePosition ASC")
    fun getDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE mangaId = :mangaId ORDER BY queuePosition ASC")
    fun getDownloadsForManga(mangaId: String): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status IN ('QUEUED', 'IN_PROGRESS') ORDER BY queuePosition ASC")
    fun getActiveDownloadsFlow(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status IN ('QUEUED', 'IN_PROGRESS') ORDER BY queuePosition ASC")
    suspend fun getActiveDownloads(): List<DownloadEntity>

    @Query("SELECT * FROM downloads WHERE status = 'QUEUED' ORDER BY queuePosition ASC LIMIT :limit")
    suspend fun getNextDownloads(limit: Int): List<DownloadEntity>

    @Query("UPDATE downloads SET status = :status WHERE chapterId = :chapterId")
    suspend fun updateStatus(chapterId: String, status: DownloadStatus)

    @Query("UPDATE downloads SET progress = :progress, downloadedPages = :downloadedPages, dateUpdated = CURRENT_TIMESTAMP WHERE chapterId = :chapterId")
    suspend fun updateProgress(chapterId: String, progress: Float, downloadedPages: Int)

    @Query("UPDATE downloads SET totalPages = :totalPages WHERE chapterId = :chapterId")
    suspend fun updateTotalPages(chapterId: String, totalPages: Int)

    @Query("SELECT status FROM downloads WHERE chapterId = :chapterId")
    suspend fun getDownloadStatus(chapterId: String): DownloadStatus?

    @Query("SELECT COALESCE(MAX(queuePosition), 0) FROM downloads")
    suspend fun getMaxQueuePosition(): Int

    @Query("UPDATE downloads SET status = 'QUEUED' WHERE status = 'PAUSED'")
    suspend fun updatePausedStatus(status: DownloadStatus = DownloadStatus.QUEUED)

    @Query("UPDATE downloads SET status = 'PAUSED' WHERE status IN ('QUEUED', 'IN_PROGRESS')")
    suspend fun updateQueuedStatus(status: DownloadStatus = DownloadStatus.PAUSED)

    @Transaction
    suspend fun reorderQueue(chapterId: String, newPosition: Int) {
        val download = getDownloadByChapterId(chapterId) ?: return
        val oldPosition = download.queuePosition
        
        if (oldPosition < newPosition) {
            // Moving down in the queue
            // Shift up all downloads between old and new positions
            updatePositionsInRange(oldPosition + 1, newPosition, -1)
        } else if (oldPosition > newPosition) {
            // Moving up in the queue
            // Shift down all downloads between new and old positions
            updatePositionsInRange(newPosition, oldPosition - 1, 1)
        } else {
            // No change needed
            return
        }
        
        // Update the position of the target download
        updatePosition(chapterId, newPosition)
    }

    @Query("UPDATE downloads SET queuePosition = queuePosition + :offset WHERE queuePosition BETWEEN :start AND :end")
    suspend fun updatePositionsInRange(start: Int, end: Int, offset: Int)

    @Query("UPDATE downloads SET queuePosition = :position WHERE chapterId = :chapterId")
    suspend fun updatePosition(chapterId: String, position: Int)
}