package com.mangaverse.reader.core.data.repository

import com.mangaverse.reader.core.data.database.dao.BookmarkDao
import com.mangaverse.reader.core.data.database.entity.BookmarkEntity
import com.mangaverse.reader.core.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing bookmarks
 */
@Singleton
class BookmarkRepository @Inject constructor(
    private val bookmarkDao: BookmarkDao
) {

    /**
     * Get all bookmarks as a Flow
     */
    fun getBookmarksFlow(): Flow<List<Bookmark>> {
        return bookmarkDao.getBookmarksFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Get bookmarks for a specific manga as a Flow
     */
    fun getMangaBookmarksFlow(mangaId: String): Flow<List<Bookmark>> {
        return bookmarkDao.getMangaBookmarksFlow(mangaId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Get bookmarks for a specific chapter as a Flow
     */
    fun getChapterBookmarksFlow(chapterId: String): Flow<List<Bookmark>> {
        return bookmarkDao.getChapterBookmarksFlow(chapterId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * Get a bookmark by ID
     */
    suspend fun getBookmark(id: String): Bookmark? {
        return bookmarkDao.getBookmarkById(id)?.toDomain()
    }

    /**
     * Add a new bookmark
     */
    suspend fun addBookmark(
        mangaId: String,
        chapterId: String,
        pageNumber: Int,
        title: String,
        description: String = "",
        thumbnailPath: String? = null
    ): String {
        val bookmark = BookmarkEntity(
            mangaId = mangaId,
            chapterId = chapterId,
            pageNumber = pageNumber,
            title = title,
            description = description,
            createdAt = Date(),
            thumbnailPath = thumbnailPath
        )
        
        bookmarkDao.insert(bookmark)
        return bookmark.id
    }

    /**
     * Update an existing bookmark
     */
    suspend fun updateBookmark(
        id: String,
        title: String? = null,
        description: String? = null,
        thumbnailPath: String? = null
    ): Boolean {
        val bookmark = bookmarkDao.getBookmarkById(id) ?: return false
        
        val updatedBookmark = bookmark.copy(
            title = title ?: bookmark.title,
            description = description ?: bookmark.description,
            thumbnailPath = thumbnailPath ?: bookmark.thumbnailPath
        )
        
        bookmarkDao.update(updatedBookmark)
        return true
    }

    /**
     * Delete a bookmark by ID
     */
    suspend fun deleteBookmark(id: String) {
        bookmarkDao.deleteById(id)
    }

    /**
     * Delete all bookmarks for a manga
     */
    suspend fun deleteMangaBookmarks(mangaId: String) {
        bookmarkDao.deleteByMangaId(mangaId)
    }

    /**
     * Delete all bookmarks for a chapter
     */
    suspend fun deleteChapterBookmarks(chapterId: String) {
        bookmarkDao.deleteByChapterId(chapterId)
    }

    /**
     * Check if a page is bookmarked
     */
    suspend fun isPageBookmarked(chapterId: String, pageNumber: Int): Boolean {
        return bookmarkDao.getBookmarkByPage(chapterId, pageNumber) != null
    }

    /**
     * Get bookmark for a specific page
     */
    suspend fun getPageBookmark(chapterId: String, pageNumber: Int): Bookmark? {
        return bookmarkDao.getBookmarkByPage(chapterId, pageNumber)?.toDomain()
    }

    /**
     * Extension function to convert entity to domain model
     */
    private fun BookmarkEntity.toDomain(): Bookmark {
        return Bookmark(
            id = id,
            mangaId = mangaId,
            chapterId = chapterId,
            pageNumber = pageNumber,
            title = title,
            description = description,
            createdAt = createdAt,
            thumbnailPath = thumbnailPath
        )
    }
}