package com.mangaverse.reader.core.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mangaverse.reader.core.data.database.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Data Access Object for chapter operations
 */
@Dao
interface ChapterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chapter: ChapterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chapters: List<ChapterEntity>)

    @Update
    suspend fun update(chapter: ChapterEntity)

    @Delete
    suspend fun delete(chapter: ChapterEntity)

    @Query("DELETE FROM chapters WHERE id = :chapterId")
    suspend fun deleteById(chapterId: String)

    @Query("DELETE FROM chapters WHERE mangaId = :mangaId")
    suspend fun deleteByMangaId(mangaId: String)

    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    suspend fun getChapterById(chapterId: String): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    fun getChapterByIdFlow(chapterId: String): Flow<ChapterEntity?>

    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId ORDER BY chapterNumber ASC")
    fun getChaptersByMangaId(mangaId: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId ORDER BY chapterNumber ASC")
    fun getChaptersByMangaIdPaged(mangaId: String): PagingSource<Int, ChapterEntity>

    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId AND downloaded = 1 ORDER BY chapterNumber ASC")
    fun getDownloadedChaptersByMangaId(mangaId: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId AND bookmarked = 1 ORDER BY chapterNumber ASC")
    fun getBookmarkedChaptersByMangaId(mangaId: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE readAt IS NOT NULL ORDER BY readAt DESC LIMIT :limit")
    fun getRecentlyReadChapters(limit: Int): Flow<List<ChapterEntity>>

    @Query("UPDATE chapters SET lastPageRead = :pageNumber, readAt = :readAt WHERE id = :chapterId")
    suspend fun updateReadProgress(chapterId: String, pageNumber: Int, readAt: Date)

    @Query("UPDATE chapters SET bookmarked = :bookmarked WHERE id = :chapterId")
    suspend fun updateBookmarkStatus(chapterId: String, bookmarked: Boolean)

    @Query("UPDATE chapters SET downloaded = :downloaded, downloadDate = :downloadDate WHERE id = :chapterId")
    suspend fun updateDownloadStatus(chapterId: String, downloaded: Boolean, downloadDate: Date?)

    @Query("SELECT COUNT(*) FROM chapters WHERE mangaId = :mangaId")
    suspend fun getChapterCount(mangaId: String): Int

    @Query("SELECT COUNT(*) FROM chapters WHERE mangaId = :mangaId AND downloaded = 1")
    suspend fun getDownloadedChapterCount(mangaId: String): Int

    @Query("SELECT COUNT(*) FROM chapters WHERE mangaId = :mangaId AND readAt IS NOT NULL")
    suspend fun getReadChapterCount(mangaId: String): Int

    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId AND chapterNumber > :chapterNumber ORDER BY chapterNumber ASC LIMIT 1")
    suspend fun getNextChapter(mangaId: String, chapterNumber: Float): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId AND chapterNumber < :chapterNumber ORDER BY chapterNumber DESC LIMIT 1")
    suspend fun getPreviousChapter(mangaId: String, chapterNumber: Float): ChapterEntity?
}