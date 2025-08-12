package com.mangaverse.reader.core.download

import android.content.Context
import androidx.work.*
import com.mangaverse.reader.core.data.database.dao.ChapterDao
import com.mangaverse.reader.core.data.database.dao.DownloadDao
import com.mangaverse.reader.core.data.database.dao.MangaDao
import com.mangaverse.reader.core.data.database.dao.PageDao
import com.mangaverse.reader.core.data.database.entity.DownloadEntity
import com.mangaverse.reader.core.data.database.entity.DownloadStatus
import com.mangaverse.reader.core.domain.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for handling manga chapter downloads
 */
@Singleton
class DownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager,
    private val downloadDao: DownloadDao,
    private val mangaDao: MangaDao,
    private val chapterDao: ChapterDao,
    private val pageDao: PageDao
) {

    companion object {
        private const val DOWNLOAD_WORK_NAME = "manga_download_work"
        private const val MAX_CONCURRENT_DOWNLOADS = 2
    }

    /**
     * Queue a chapter for download
     */
    suspend fun queueDownload(mangaId: String, chapterId: String): Result<Unit> {
        return try {
            // Check if chapter exists
            val chapter = chapterDao.getChapterById(chapterId)
                ?: return Result.Error(Exception("Chapter not found"))
            
            // Check if already downloaded
            if (chapter.downloaded) {
                return Result.Success(Unit)
            }
            
            // Check if already in queue
            val existingDownload = downloadDao.getDownloadByChapterId(chapterId)
            if (existingDownload != null) {
                // If failed, reset status to queued
                if (existingDownload.status == DownloadStatus.ERROR) {
                    downloadDao.updateStatus(chapterId, DownloadStatus.QUEUED)
                }
                return Result.Success(Unit)
            }
            
            // Get next queue position
            val nextPosition = downloadDao.getMaxQueuePosition() + 1
            
            // Create download entity
            val download = DownloadEntity(
                id = UUID.randomUUID().toString(),
                chapterId = chapterId,
                mangaId = mangaId,
                status = DownloadStatus.QUEUED,
                progress = 0f,
                totalPages = chapter.pageCount,
                downloadedPages = 0,
                queuePosition = nextPosition,
                dateAdded = Date(),
                dateUpdated = Date()
            )
            
            // Save to database
            downloadDao.insert(download)
            
            // Start download worker if not already running
            enqueueDownloadWorker()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Queue multiple chapters for download
     */
    suspend fun queueDownloads(mangaId: String, chapterIds: List<String>): Result<Unit> {
        return try {
            chapterIds.forEach { chapterId ->
                queueDownload(mangaId, chapterId)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Cancel a download
     */
    suspend fun cancelDownload(chapterId: String): Result<Unit> {
        return try {
            val download = downloadDao.getDownloadByChapterId(chapterId)
                ?: return Result.Error(Exception("Download not found"))
            
            // If download is in progress, it will be stopped by the worker
            downloadDao.updateStatus(chapterId, DownloadStatus.CANCELLED)
            
            // Delete any partially downloaded pages
            val pages = pageDao.getPagesByChapterIdSync(chapterId)
            pages.forEach { page ->
                if (page.filePath != null) {
                    // Delete file
                    val file = java.io.File(page.filePath)
                    if (file.exists()) {
                        file.delete()
                    }
                    
                    // Clear file path
                    pageDao.updateFilePath(page.id, "", null)
                }
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Cancel all downloads
     */
    suspend fun cancelAllDownloads(): Result<Unit> {
        return try {
            // Get all active downloads
            val downloads = downloadDao.getActiveDownloads()
            
            // Cancel each download
            downloads.forEach { download ->
                cancelDownload(download.chapterId)
            }
            
            // Cancel worker
            workManager.cancelUniqueWork(DOWNLOAD_WORK_NAME)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Pause all downloads
     */
    suspend fun pauseAllDownloads(): Result<Unit> {
        return try {
            // Update status of queued and in-progress downloads
            downloadDao.updateQueuedStatus(DownloadStatus.PAUSED)
            
            // Cancel worker
            workManager.cancelUniqueWork(DOWNLOAD_WORK_NAME)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Resume all downloads
     */
    suspend fun resumeAllDownloads(): Result<Unit> {
        return try {
            // Update status of paused downloads
            downloadDao.updatePausedStatus(DownloadStatus.QUEUED)
            
            // Start worker
            enqueueDownloadWorker()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Reorder download queue
     */
    suspend fun reorderQueue(chapterId: String, newPosition: Int): Result<Unit> {
        return try {
            downloadDao.reorderQueue(chapterId, newPosition)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get all downloads
     */
    fun getDownloads(): Flow<List<DownloadEntity>> {
        return downloadDao.getDownloads()
    }

    /**
     * Get downloads for a manga
     */
    fun getDownloadsForManga(mangaId: String): Flow<List<DownloadEntity>> {
        return downloadDao.getDownloadsForManga(mangaId)
    }

    /**
     * Get active downloads
     */
    fun getActiveDownloads(): Flow<List<DownloadEntity>> {
        return downloadDao.getActiveDownloadsFlow()
    }

    /**
     * Start download worker
     */
    private fun enqueueDownloadWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val workRequest = PeriodicWorkRequestBuilder<DownloadWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            DOWNLOAD_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}