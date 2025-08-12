package com.mangaverse.reader.core.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mangaverse.reader.core.data.database.entity.MangaEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Data Access Object for manga operations
 */
@Dao
interface MangaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(manga: MangaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mangas: List<MangaEntity>)

    @Update
    suspend fun update(manga: MangaEntity)

    @Delete
    suspend fun delete(manga: MangaEntity)

    @Query("DELETE FROM manga WHERE id = :mangaId")
    suspend fun deleteById(mangaId: String)

    @Query("SELECT * FROM manga WHERE id = :mangaId")
    suspend fun getMangaById(mangaId: String): MangaEntity?

    @Query("SELECT * FROM manga WHERE id = :mangaId")
    fun getMangaByIdFlow(mangaId: String): Flow<MangaEntity?>

    @Query("SELECT * FROM manga ORDER BY lastUpdated DESC")
    fun getAllManga(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE inLibrary = 1 ORDER BY lastUpdated DESC")
    fun getLibraryManga(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE inLibrary = 1 ORDER BY lastUpdated DESC")
    fun getLibraryMangaPaged(): PagingSource<Int, MangaEntity>

    @Query("SELECT * FROM manga WHERE favorite = 1 ORDER BY lastUpdated DESC")
    fun getFavoriteManga(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE title LIKE '%' || :query || '%' ORDER BY lastUpdated DESC")
    fun searchManga(query: String): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE title LIKE '%' || :query || '%' ORDER BY lastUpdated DESC")
    fun searchMangaPaged(query: String): PagingSource<Int, MangaEntity>

    @Query("SELECT * FROM manga WHERE :genre IN (SELECT value FROM json_each(genres)) ORDER BY lastUpdated DESC")
    fun getMangaByGenre(genre: String): Flow<List<MangaEntity>>

    @Query("UPDATE manga SET inLibrary = :inLibrary WHERE id = :mangaId")
    suspend fun updateLibraryStatus(mangaId: String, inLibrary: Boolean)

    @Query("UPDATE manga SET favorite = :favorite WHERE id = :mangaId")
    suspend fun updateFavoriteStatus(mangaId: String, favorite: Boolean)

    @Query("UPDATE manga SET lastRead = :date WHERE id = :mangaId")
    suspend fun updateLastRead(mangaId: String, date: Date)

    @Query("UPDATE manga SET downloadCount = downloadCount + 1 WHERE id = :mangaId")
    suspend fun incrementDownloadCount(mangaId: String)

    @Query("UPDATE manga SET downloadCount = downloadCount - 1 WHERE id = :mangaId")
    suspend fun decrementDownloadCount(mangaId: String)

    @Query("SELECT * FROM manga WHERE lastRead IS NOT NULL ORDER BY lastRead DESC LIMIT :limit")
    fun getRecentlyRead(limit: Int): Flow<List<MangaEntity>>

    @Query("SELECT * FROM manga WHERE downloadCount > 0 ORDER BY downloadCount DESC")
    fun getDownloadedManga(): Flow<List<MangaEntity>>
}