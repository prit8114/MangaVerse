package com.mangaverse.reader.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mangaverse.reader.core.data.database.dao.ChapterDao
import com.mangaverse.reader.core.data.database.dao.PageDao
import com.mangaverse.reader.core.data.database.entity.ChapterEntity
import com.mangaverse.reader.core.data.database.entity.PageEntity
import com.mangaverse.reader.core.data.network.api.MangaDexApi
import com.mangaverse.reader.core.domain.model.Chapter
import com.mangaverse.reader.core.domain.model.Page
import com.mangaverse.reader.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for chapter operations
 */
@Singleton
class ChapterRepository @Inject constructor(
    private val mangaDexApi: MangaDexApi,
    private val chapterDao: ChapterDao,
    private val pageDao: PageDao,
    private val chapterMapper: ChapterMapper,
    private val pageMapper: PageMapper
) {

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }

    /**
     * Get chapter by ID
     */
    suspend fun getChapter(id: String): Result<Chapter> {
        return try {
            // Try to get from local database first
            val localChapter = chapterDao.getChapterById(id)
            
            if (localChapter != null) {
                Result.Success(chapterMapper.mapEntityToDomain(localChapter))
            } else {
                // Fetch from network if not in database
                val response = mangaDexApi.getChapter(id)
                val chapterData = response.data
                val chapter = chapterMapper.mapResponseToDomain(chapterData)
                
                // Save to database
                chapterDao.insert(chapterMapper.mapDomainToEntity(chapter))
                
                Result.Success(chapter)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get chapters for a manga
     */
    fun getChapters(mangaId: String): Flow<List<Chapter>> {
        return chapterDao.getChaptersByMangaId(mangaId).map { list ->
            list.map { chapterEntity ->
                chapterMapper.mapEntityToDomain(chapterEntity)
            }
        }
    }

    /**
     * Get chapters for a manga with paging
     */
    fun getChaptersPaged(mangaId: String): Flow<PagingData<Chapter>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { chapterDao.getChaptersByMangaIdPaged(mangaId) }
        ).flow.map { pagingData ->
            pagingData.map { chapterEntity ->
                chapterMapper.mapEntityToDomain(chapterEntity)
            }
        }
    }

    /**
     * Get downloaded chapters for a manga
     */
    fun getDownloadedChapters(mangaId: String): Flow<List<Chapter>> {
        return chapterDao.getDownloadedChaptersByMangaId(mangaId).map { list ->
            list.map { chapterEntity ->
                chapterMapper.mapEntityToDomain(chapterEntity)
            }
        }
    }

    /**
     * Get bookmarked chapters for a manga
     */
    fun getBookmarkedChapters(mangaId: String): Flow<List<Chapter>> {
        return chapterDao.getBookmarkedChaptersByMangaId(mangaId).map { list ->
            list.map { chapterEntity ->
                chapterMapper.mapEntityToDomain(chapterEntity)
            }
        }
    }

    /**
     * Get recently read chapters
     */
    fun getRecentlyReadChapters(limit: Int = 10): Flow<List<Chapter>> {
        return chapterDao.getRecentlyReadChapters(limit).map { list ->
            list.map { chapterEntity ->
                chapterMapper.mapEntityToDomain(chapterEntity)
            }
        }
    }

    /**
     * Update reading progress for a chapter
     */
    suspend fun updateReadProgress(chapterId: String, pageNumber: Int): Result<Unit> {
        return try {
            chapterDao.updateReadProgress(chapterId, pageNumber, Date())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Toggle bookmark status for a chapter
     */
    suspend fun toggleBookmark(chapterId: String, bookmarked: Boolean): Result<Unit> {
        return try {
            chapterDao.updateBookmarkStatus(chapterId, bookmarked)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Update download status for a chapter
     */
    suspend fun updateDownloadStatus(chapterId: String, downloaded: Boolean): Result<Unit> {
        return try {
            chapterDao.updateDownloadStatus(chapterId, downloaded, if (downloaded) Date() else null)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get chapter count for a manga
     */
    suspend fun getChapterCount(mangaId: String): Result<Int> {
        return try {
            val count = chapterDao.getChapterCount(mangaId)
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get downloaded chapter count for a manga
     */
    suspend fun getDownloadedChapterCount(mangaId: String): Result<Int> {
        return try {
            val count = chapterDao.getDownloadedChapterCount(mangaId)
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get read chapter count for a manga
     */
    suspend fun getReadChapterCount(mangaId: String): Result<Int> {
        return try {
            val count = chapterDao.getReadChapterCount(mangaId)
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get next chapter
     */
    suspend fun getNextChapter(mangaId: String, chapterNumber: Float): Result<Chapter?> {
        return try {
            val nextChapter = chapterDao.getNextChapter(mangaId, chapterNumber)
            Result.Success(nextChapter?.let { chapterMapper.mapEntityToDomain(it) })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get previous chapter
     */
    suspend fun getPreviousChapter(mangaId: String, chapterNumber: Float): Result<Chapter?> {
        return try {
            val previousChapter = chapterDao.getPreviousChapter(mangaId, chapterNumber)
            Result.Success(previousChapter?.let { chapterMapper.mapEntityToDomain(it) })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Fetch chapter pages from API and save to database
     */
    suspend fun fetchChapterPages(chapterId: String): Result<List<Page>> {
        return try {
            // Get chapter info first
            val chapterResponse = mangaDexApi.getChapter(chapterId)
            val chapterData = chapterResponse.data
            
            // Get pages from at-home server
            val atHomeResponse = mangaDexApi.getChapterPages(chapterId)
            
            // Create page entities
            val pages = atHomeResponse.chapter.data.mapIndexed { index, filename ->
                val pageUrl = "${atHomeResponse.baseUrl}/data/${atHomeResponse.chapter.hash}/$filename"
                val dataSaverUrl = "${atHomeResponse.baseUrl}/data-saver/${atHomeResponse.chapter.hash}/${atHomeResponse.chapter.dataSaver[index]}"
                
                PageEntity(
                    id = "$chapterId-$index",
                    chapterId = chapterId,
                    pageNumber = index + 1,
                    imageUrl = pageUrl,
                    dataSaverUrl = dataSaverUrl,
                    filePath = null,
                    width = 0,
                    height = 0,
                    encryptionKey = null,
                    detectedPanels = null
                )
            }
            
            // Save pages to database
            pageDao.insertAll(pages)
            
            // Return domain models
            Result.Success(pages.map { pageMapper.mapEntityToDomain(it) })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get pages for a chapter
     */
    fun getPages(chapterId: String): Flow<List<Page>> {
        return pageDao.getPagesByChapterId(chapterId).map { list ->
            list.map { pageEntity ->
                pageMapper.mapEntityToDomain(pageEntity)
            }
        }
    }

    /**
     * Get pages for a chapter synchronously
     */
    suspend fun getPagesSync(chapterId: String): Result<List<Page>> {
        return try {
            val pages = pageDao.getPagesByChapterIdSync(chapterId)
            Result.Success(pages.map { pageMapper.mapEntityToDomain(it) })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get page count for a chapter
     */
    suspend fun getPageCount(chapterId: String): Result<Int> {
        return try {
            val count = pageDao.getPageCount(chapterId)
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Update file path for a downloaded page
     */
    suspend fun updatePageFilePath(pageId: String, filePath: String, encryptionKey: String?): Result<Unit> {
        return try {
            pageDao.updateFilePath(pageId, filePath, encryptionKey)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Update dimensions for a page
     */
    suspend fun updatePageDimensions(pageId: String, width: Int, height: Int): Result<Unit> {
        return try {
            pageDao.updateDimensions(pageId, width, height)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Update detected panels for a page
     */
    suspend fun updateDetectedPanels(pageId: String, detectedPanels: String?): Result<Unit> {
        return try {
            pageDao.updateDetectedPanels(pageId, detectedPanels)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}