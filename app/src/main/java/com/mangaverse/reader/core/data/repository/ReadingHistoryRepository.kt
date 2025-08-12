package com.mangaverse.reader.core.data.repository

import com.mangaverse.reader.core.data.database.dao.ReadingHistoryDao
import com.mangaverse.reader.core.data.database.entity.ReadingHistoryEntity
import com.mangaverse.reader.core.data.network.api.MangaDexApi
import com.mangaverse.reader.core.data.network.model.ReadingHistoryRequest
import com.mangaverse.reader.core.domain.model.ReadingHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing reading history
 */
@Singleton
class ReadingHistoryRepository @Inject constructor(
    private val readingHistoryDao: ReadingHistoryDao,
    private val mangaDexApi: MangaDexApi,
    private val authRepository: AuthRepository
) {

    /**
     * Get reading history as a Flow
     */
    fun getReadingHistoryFlow(limit: Int = 50): Flow<List<ReadingHistory>> {
        return readingHistoryDao.getReadingHistoryFlow(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Get reading history for a specific manga as a Flow
     */
    fun getMangaReadingHistoryFlow(mangaId: String): Flow<List<ReadingHistory>> {
        return readingHistoryDao.getMangaReadingHistoryFlow(mangaId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Get reading history for a specific chapter
     */
    suspend fun getChapterReadingHistory(chapterId: String): ReadingHistory? {
        return readingHistoryDao.getChapterReadingHistory(chapterId)?.toDomain()
    }

    /**
     * Add or update reading history entry
     */
    suspend fun addOrUpdateReadingHistory(
        mangaId: String,
        chapterId: String,
        pageNumber: Int,
        sessionDuration: Long
    ) {
        val timestamp = Date()
        
        // Update local database
        val existingEntry = readingHistoryDao.getChapterReadingHistory(chapterId)
        if (existingEntry != null) {
            readingHistoryDao.updateReadingHistory(
                chapterId = chapterId,
                pageNumber = pageNumber,
                timestamp = timestamp,
                sessionDuration = existingEntry.sessionDuration + sessionDuration
            )
        } else {
            readingHistoryDao.insert(
                ReadingHistoryEntity(
                    mangaId = mangaId,
                    chapterId = chapterId,
                    pageNumber = pageNumber,
                    timestamp = timestamp,
                    sessionDuration = sessionDuration
                )
            )
        }
        
        // Sync with MangaDex if user is logged in
        if (authRepository.isLoggedIn()) {
            try {
                mangaDexApi.updateReadingHistory(ReadingHistoryRequest(
                    chapterId = chapterId,
                    lastPage = pageNumber
                ))
            } catch (e: Exception) {
                // Ignore network errors, we'll sync later
            }
        }
    }

    /**
     * Delete reading history entry
     */
    suspend fun deleteReadingHistory(id: String) {
        readingHistoryDao.deleteById(id)
    }

    /**
     * Delete all reading history for a manga
     */
    suspend fun deleteMangaReadingHistory(mangaId: String) {
        readingHistoryDao.deleteByMangaId(mangaId)
    }

    /**
     * Delete all reading history
     */
    suspend fun deleteAllReadingHistory() {
        readingHistoryDao.deleteAll()
    }

    /**
     * Sync reading history with MangaDex
     */
    suspend fun syncReadingHistory() {
        if (!authRepository.isLoggedIn()) return
        
        try {
            val response = mangaDexApi.getReadingHistory()
            // Process response and update local database
            // This is a simplified implementation
        } catch (e: Exception) {
            // Handle error
        }
    }

    /**
     * Extension function to convert entity to domain model
     */
    private fun ReadingHistoryEntity.toDomain(): ReadingHistory {
        return ReadingHistory(
            id = id,
            mangaId = mangaId,
            chapterId = chapterId,
            pageNumber = pageNumber,
            timestamp = timestamp,
            sessionDuration = sessionDuration
        )
    }
}