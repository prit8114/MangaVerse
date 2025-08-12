package com.mangaverse.reader.core.domain.usecase

import com.mangaverse.reader.core.data.repository.ReadingHistoryRepository
import com.mangaverse.reader.core.domain.model.ReadingHistory
import com.mangaverse.reader.core.domain.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Container for all reading history-related use cases
 */
data class ReadingHistoryUseCases(
    val getReadingHistory: GetReadingHistoryUseCase,
    val getMangaReadingHistory: GetMangaReadingHistoryUseCase,
    val getChapterReadingHistory: GetChapterReadingHistoryUseCase,
    val addOrUpdateReadingHistory: AddOrUpdateReadingHistoryUseCase,
    val deleteReadingHistory: DeleteReadingHistoryUseCase,
    val deleteMangaReadingHistory: DeleteMangaReadingHistoryUseCase,
    val deleteAllReadingHistory: DeleteAllReadingHistoryUseCase,
    val syncReadingHistory: SyncReadingHistoryUseCase
)

/**
 * Use case to get reading history
 */
class GetReadingHistoryUseCase @Inject constructor(
    private val readingHistoryRepository: ReadingHistoryRepository
) {
    operator fun invoke(limit: Int = 50): Flow<List<ReadingHistory>> {
        return readingHistoryRepository.getReadingHistoryFlow(limit)
    }
}

/**
 * Use case to get reading history for a manga
 */
class GetMangaReadingHistoryUseCase @Inject constructor(
    private val readingHistoryRepository: ReadingHistoryRepository
) {
    operator fun invoke(mangaId: String): Flow<List<ReadingHistory>> {
        return readingHistoryRepository.getMangaReadingHistoryFlow(mangaId)
    }
}

/**
 * Use case to get reading history for a chapter
 */
class GetChapterReadingHistoryUseCase @Inject constructor(
    private val readingHistoryRepository: ReadingHistoryRepository
) {
    suspend operator fun invoke(chapterId: String): Result<ReadingHistory?> {
        return try {
            val history = readingHistoryRepository.getChapterReadingHistory(chapterId)
            Result.Success(history)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Use case to add or update reading history
 */
class AddOrUpdateReadingHistoryUseCase @Inject constructor(
    private val readingHistoryRepository: ReadingHistoryRepository
) {
    suspend operator fun invoke(
        mangaId: String,
        chapterId: String,
        pageNumber: Int,
        sessionDuration: Long
    ): Result<Unit> {
        return try {
            readingHistoryRepository.addOrUpdateReadingHistory(
                mangaId = mangaId,
                chapterId = chapterId,
                pageNumber = pageNumber,
                sessionDuration = sessionDuration
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Use case to delete reading history
 */
class DeleteReadingHistoryUseCase @Inject constructor(
    private val readingHistoryRepository: ReadingHistoryRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return try {
            readingHistoryRepository.deleteReadingHistory(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Use case to delete reading history for a manga
 */
class DeleteMangaReadingHistoryUseCase @Inject constructor(
    private val readingHistoryRepository: ReadingHistoryRepository
) {
    suspend operator fun invoke(mangaId: String): Result<Unit> {
        return try {
            readingHistoryRepository.deleteMangaReadingHistory(mangaId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Use case to delete all reading history
 */
class DeleteAllReadingHistoryUseCase @Inject constructor(
    private val readingHistoryRepository: ReadingHistoryRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            readingHistoryRepository.deleteAllReadingHistory()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Use case to sync reading history with remote server
 */
class SyncReadingHistoryUseCase @Inject constructor(
    private val readingHistoryRepository: ReadingHistoryRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            readingHistoryRepository.syncReadingHistory()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}