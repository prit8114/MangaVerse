package com.mangaverse.reader.core.domain.usecase

import com.mangaverse.reader.core.data.database.entity.DownloadEntity
import com.mangaverse.reader.core.data.repository.DownloadManager
import com.mangaverse.reader.core.domain.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Container for all download-related use cases
 */
data class DownloadUseCases(
    val queueDownload: QueueDownloadUseCase,
    val queueMultipleDownloads: QueueMultipleDownloadsUseCase,
    val cancelDownload: CancelDownloadUseCase,
    val cancelAllDownloads: CancelAllDownloadsUseCase,
    val pauseAllDownloads: PauseAllDownloadsUseCase,
    val resumeAllDownloads: ResumeAllDownloadsUseCase,
    val getDownloads: GetDownloadsUseCase,
    val getDownloadsForManga: GetDownloadsForMangaUseCase,
    val getActiveDownloads: GetActiveDownloadsUseCase,
    val reorderDownload: ReorderDownloadUseCase
)

/**
 * Use case to queue a chapter download
 */
class QueueDownloadUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    suspend operator fun invoke(mangaId: String, chapterId: String): Result<Unit> {
        return downloadManager.queueDownload(mangaId, chapterId)
    }
}

/**
 * Use case to queue multiple chapter downloads
 */
class QueueMultipleDownloadsUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    suspend operator fun invoke(mangaId: String, chapterIds: List<String>): Result<Unit> {
        return downloadManager.queueDownloads(mangaId, chapterIds)
    }
}

/**
 * Use case to cancel a download
 */
class CancelDownloadUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    suspend operator fun invoke(chapterId: String): Result<Unit> {
        return downloadManager.cancelDownload(chapterId)
    }
}

/**
 * Use case to cancel all downloads
 */
class CancelAllDownloadsUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    suspend operator fun invoke(): Result<Unit> {
        return downloadManager.cancelAllDownloads()
    }
}

/**
 * Use case to pause all downloads
 */
class PauseAllDownloadsUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    suspend operator fun invoke(): Result<Unit> {
        return downloadManager.pauseAllDownloads()
    }
}

/**
 * Use case to resume all downloads
 */
class ResumeAllDownloadsUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    suspend operator fun invoke(): Result<Unit> {
        return downloadManager.resumeAllDownloads()
    }
}

/**
 * Use case to get all downloads
 */
class GetDownloadsUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    operator fun invoke(): Flow<List<DownloadEntity>> {
        return downloadManager.getDownloads()
    }
}

/**
 * Use case to get downloads for a manga
 */
class GetDownloadsForMangaUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    operator fun invoke(mangaId: String): Flow<List<DownloadEntity>> {
        return downloadManager.getDownloadsForManga(mangaId)
    }
}

/**
 * Use case to get active downloads
 */
class GetActiveDownloadsUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    operator fun invoke(): Flow<List<DownloadEntity>> {
        return downloadManager.getActiveDownloads()
    }
}

/**
 * Use case to reorder a download in the queue
 */
class ReorderDownloadUseCase @Inject constructor(
    private val downloadManager: DownloadManager
) {
    suspend operator fun invoke(chapterId: String, newPosition: Int): Result<Unit> {
        return downloadManager.reorderDownload(chapterId, newPosition)
    }
}