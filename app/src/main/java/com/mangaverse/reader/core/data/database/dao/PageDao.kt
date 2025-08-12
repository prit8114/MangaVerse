package com.mangaverse.reader.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mangaverse.reader.core.data.database.entity.PageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for page operations
 */
@Dao
interface PageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(page: PageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pages: List<PageEntity>)

    @Update
    suspend fun update(page: PageEntity)

    @Delete
    suspend fun delete(page: PageEntity)

    @Query("DELETE FROM pages WHERE id = :pageId")
    suspend fun deleteById(pageId: String)

    @Query("DELETE FROM pages WHERE chapterId = :chapterId")
    suspend fun deleteByChapterId(chapterId: String)

    @Query("SELECT * FROM pages WHERE id = :pageId")
    suspend fun getPageById(pageId: String): PageEntity?

    @Query("SELECT * FROM pages WHERE chapterId = :chapterId ORDER BY pageNumber ASC")
    fun getPagesByChapterId(chapterId: String): Flow<List<PageEntity>>

    @Query("SELECT * FROM pages WHERE chapterId = :chapterId ORDER BY pageNumber ASC")
    suspend fun getPagesByChapterIdSync(chapterId: String): List<PageEntity>

    @Query("SELECT * FROM pages WHERE chapterId = :chapterId AND pageNumber = :pageNumber")
    suspend fun getPageByNumber(chapterId: String, pageNumber: Int): PageEntity?

    @Query("SELECT COUNT(*) FROM pages WHERE chapterId = :chapterId")
    suspend fun getPageCount(chapterId: String): Int

    @Query("SELECT COUNT(*) FROM pages WHERE chapterId = :chapterId AND filePath IS NOT NULL")
    suspend fun getDownloadedPageCount(chapterId: String): Int

    @Query("UPDATE pages SET filePath = :filePath, encryptionKey = :encryptionKey WHERE id = :pageId")
    suspend fun updateFilePath(pageId: String, filePath: String, encryptionKey: String?)

    @Query("UPDATE pages SET width = :width, height = :height WHERE id = :pageId")
    suspend fun updateDimensions(pageId: String, width: Int, height: Int)

    @Query("UPDATE pages SET detectedPanels = :detectedPanels WHERE id = :pageId")
    suspend fun updateDetectedPanels(pageId: String, detectedPanels: String?)
}