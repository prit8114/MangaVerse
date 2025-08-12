package com.mangaverse.reader.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mangaverse.reader.core.data.database.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for bookmark operations
 */
@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Update
    suspend fun update(bookmark: BookmarkEntity)

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM bookmarks WHERE mangaId = :mangaId")
    suspend fun deleteByMangaId(mangaId: String)

    @Query("DELETE FROM bookmarks WHERE chapterId = :chapterId")
    suspend fun deleteByChapterId(chapterId: String)

    @Query("SELECT * FROM bookmarks WHERE id = :id")
    suspend fun getBookmarkById(id: String): BookmarkEntity?

    @Query("SELECT * FROM bookmarks WHERE chapterId = :chapterId AND pageNumber = :pageNumber")
    suspend fun getBookmarkByPage(chapterId: String, pageNumber: Int): BookmarkEntity?

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getBookmarksFlow(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE mangaId = :mangaId ORDER BY createdAt DESC")
    fun getMangaBookmarksFlow(mangaId: String): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE chapterId = :chapterId ORDER BY pageNumber ASC")
    fun getChapterBookmarksFlow(chapterId: String): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    suspend fun getBookmarks(): List<BookmarkEntity>

    @Query("SELECT * FROM bookmarks WHERE mangaId = :mangaId ORDER BY createdAt DESC")
    suspend fun getMangaBookmarks(mangaId: String): List<BookmarkEntity>

    @Query("SELECT * FROM bookmarks WHERE chapterId = :chapterId ORDER BY pageNumber ASC")
    suspend fun getChapterBookmarks(chapterId: String): List<BookmarkEntity>

    @Query("SELECT COUNT(*) FROM bookmarks WHERE mangaId = :mangaId")
    suspend fun getBookmarkCountForManga(mangaId: String): Int

    @Query("SELECT COUNT(*) FROM bookmarks WHERE chapterId = :chapterId")
    suspend fun getBookmarkCountForChapter(chapterId: String): Int

    @Query("""
        SELECT DISTINCT mangaId FROM bookmarks 
        ORDER BY createdAt DESC LIMIT :limit
    """)
    suspend fun getRecentlyBookmarkedMangaIds(limit: Int): List<String>
}