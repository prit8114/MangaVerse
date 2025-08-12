package com.mangaverse.reader.core.download

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mangaverse.reader.core.data.database.dao.ChapterDao
import com.mangaverse.reader.core.data.database.dao.DownloadDao
import com.mangaverse.reader.core.data.database.dao.MangaDao
import com.mangaverse.reader.core.data.database.dao.PageDao
import com.mangaverse.reader.core.data.database.entity.DownloadStatus
import com.mangaverse.reader.core.data.repository.ChapterRepository
import com.mangaverse.reader.core.security.FileEncryptionService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Worker for downloading manga chapters
 */
@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val downloadDao: DownloadDao,
    private val mangaDao: MangaDao,
    private val chapterDao: ChapterDao,
    private val pageDao: PageDao,
    private val chapterRepository: ChapterRepository,
    private val fileEncryptionService: FileEncryptionService,
    private val okHttpClient: OkHttpClient
) : CoroutineWorker(context, params) {

    companion object {
        private const val MAX_CONCURRENT_DOWNLOADS = 2
    }

    override suspend fun doWork(): Result {
        try {
            // Get next downloads to process
            val downloads = downloadDao.getNextDownloads(MAX_CONCURRENT_DOWNLOADS)
            if (downloads.isEmpty()) {
                return Result.success()
            }
            
            // Process each download
            downloads.forEach { download ->
                processDownload(download.chapterId, download.mangaId)
            }
            
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    /**
     * Process a single download
     */
    private suspend fun processDownload(chapterId: String, mangaId: String) {
        try {
            // Update status to in progress
            downloadDao.updateStatus(chapterId, DownloadStatus.IN_PROGRESS)
            
            // Fetch chapter pages if not already in database
            val pageCount = pageDao.getPageCount(chapterId)
            if (pageCount == 0) {
                val result = chapterRepository.fetchChapterPages(chapterId)
                if (result is com.mangaverse.reader.core.domain.Result.Error) {
                    downloadDao.updateStatus(chapterId, DownloadStatus.ERROR)
                    return
                }
            }
            
            // Get pages
            val pages = pageDao.getPagesByChapterIdSync(chapterId)
            val totalPages = pages.size
            
            // Update total pages
            downloadDao.updateTotalPages(chapterId, totalPages)
            
            // Download each page
            var downloadedPages = 0
            for (page in pages) {
                // Check if download was cancelled
                val currentStatus = downloadDao.getDownloadStatus(chapterId)
                if (currentStatus == DownloadStatus.CANCELLED || currentStatus == DownloadStatus.PAUSED) {
                    return
                }
                
                // Skip already downloaded pages
                if (page.filePath != null && File(page.filePath).exists()) {
                    downloadedPages++
                    continue
                }
                
                // Download page
                val tempFile = withContext(Dispatchers.IO) {
                    val tempFile = File.createTempFile("manga_", ".tmp", applicationContext.cacheDir)
                    downloadImage(page.imageUrl, tempFile)
                    tempFile
                }
                
                // Create encrypted file
                val destFile = fileEncryptionService.getPageFile(
                    mangaId,
                    chapterId,
                    page.pageNumber
                )
                
                // Encrypt and save
                val encryptionKey = fileEncryptionService.encryptFile(tempFile, destFile)
                
                // Update page in database
                pageDao.updateFilePath(page.id, destFile.absolutePath, encryptionKey)
                
                // Delete temp file
                tempFile.delete()
                
                // Update progress
                downloadedPages++
                val progress = downloadedPages.toFloat() / totalPages
                downloadDao.updateProgress(chapterId, progress, downloadedPages)
            }
            
            // Update chapter status
            chapterDao.updateDownloadStatus(chapterId, true, Date())
            
            // Update manga download count
            mangaDao.incrementDownloadCount(mangaId)
            
            // Mark download as complete
            downloadDao.updateStatus(chapterId, DownloadStatus.COMPLETED)
            
        } catch (e: Exception) {
            // Mark download as failed
            downloadDao.updateStatus(chapterId, DownloadStatus.ERROR)
        }
    }

    /**
     * Download an image to a file
     */
    private suspend fun downloadImage(url: String, file: File) {
        withContext(Dispatchers.IO) {
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                throw Exception("Failed to download image: ${response.code}")
            }
            
            response.body?.let { body ->
                FileOutputStream(file).use { output ->
                    body.byteStream().use { input ->
                        input.copyTo(output)
                    }
                }
            } ?: throw Exception("Empty response body")
        }
    }
}