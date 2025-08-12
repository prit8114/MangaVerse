package com.mangaverse.reader.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mangaverse.reader.core.data.database.dao.MangaDao
import com.mangaverse.reader.core.data.database.entity.MangaEntity
import com.mangaverse.reader.core.data.network.api.MangaDexApi
import com.mangaverse.reader.core.data.paging.MangaRemoteMediator
import com.mangaverse.reader.core.domain.model.Manga
import com.mangaverse.reader.core.domain.model.MangaFilter
import com.mangaverse.reader.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for manga operations
 */
@Singleton
class MangaRepository @Inject constructor(
    private val mangaDexApi: MangaDexApi,
    private val mangaDao: MangaDao,
    private val mangaMapper: MangaMapper
) {

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }

    /**
     * Get manga by ID
     */
    suspend fun getManga(id: String): Result<Manga> {
        return try {
            // Try to get from local database first
            val localManga = mangaDao.getMangaById(id)
            
            if (localManga != null) {
                Result.Success(mangaMapper.mapEntityToDomain(localManga))
            } else {
                // Fetch from network if not in database
                val response = mangaDexApi.getManga(id)
                val mangaData = response.data
                val manga = mangaMapper.mapResponseToDomain(mangaData)
                
                // Save to database
                mangaDao.insert(mangaMapper.mapDomainToEntity(manga))
                
                Result.Success(manga)
            }
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }

    /**
     * Get manga list with paging
     */
    @OptIn(ExperimentalPagingApi::class)
    fun getMangaList(filter: MangaFilter): Flow<PagingData<Manga>> {
        val pagingSourceFactory = { mangaDao.getMangaPagingSource() }
        
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = MangaRemoteMediator(
                mangaDexApi,
                mangaDao,
                mangaMapper,
                filter
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { mangaEntity ->
                mangaMapper.mapEntityToDomain(mangaEntity)
            }
        }
    }

    /**
     * Search manga
     */
    @OptIn(ExperimentalPagingApi::class)
    fun searchManga(query: String): Flow<PagingData<Manga>> {
        val pagingSourceFactory = { mangaDao.searchManga("%$query%") }
        
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = MangaRemoteMediator(
                mangaDexApi,
                mangaDao,
                mangaMapper,
                MangaFilter(title = query)
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { mangaEntity ->
                mangaMapper.mapEntityToDomain(mangaEntity)
            }
        }
    }

    /**
     * Get manga in library
     */
    fun getLibraryManga(): Flow<List<Manga>> {
        return mangaDao.getLibraryManga().map { list ->
            list.map { mangaEntity ->
                mangaMapper.mapEntityToDomain(mangaEntity)
            }
        }
    }

    /**
     * Get favorite manga
     */
    fun getFavoriteManga(): Flow<List<Manga>> {
        return mangaDao.getFavoriteManga().map { list ->
            list.map { mangaEntity ->
                mangaMapper.mapEntityToDomain(mangaEntity)
            }
        }
    }

    /**
     * Get recently read manga
     */
    fun getRecentlyReadManga(limit: Int = 10): Flow<List<Manga>> {
        return mangaDao.getRecentlyReadManga(limit).map { list ->
            list.map { mangaEntity ->
                mangaMapper.mapEntityToDomain(mangaEntity)
            }
        }
    }

    /**
     * Get manga by genre
     */
    fun getMangaByGenre(genre: String): Flow<List<Manga>> {
        return mangaDao.getMangaByGenre(genre).map { list ->
            list.map { mangaEntity ->
                mangaMapper.mapEntityToDomain(mangaEntity)
            }
        }
    }

    /**
     * Add manga to library
     */
    suspend fun addToLibrary(mangaId: String): Result<Unit> {
        return try {
            mangaDao.updateLibraryStatus(mangaId, true)
            Result.Success(Unit)
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }

    /**
     * Remove manga from library
     */
    suspend fun removeFromLibrary(mangaId: String): Result<Unit> {
        return try {
            mangaDao.updateLibraryStatus(mangaId, false)
            Result.Success(Unit)
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }

    /**
     * Toggle favorite status
     */
    suspend fun toggleFavorite(mangaId: String, favorite: Boolean): Result<Unit> {
        return try {
            mangaDao.updateFavoriteStatus(mangaId, favorite)
            Result.Success(Unit)
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }

    /**
     * Update last read timestamp
     */
    suspend fun updateLastRead(mangaId: String): Result<Unit> {
        return try {
            mangaDao.updateLastRead(mangaId)
            Result.Success(Unit)
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }

    /**
     * Get manga tags
     */
    suspend fun getMangaTags(): Result<List<String>> {
        return try {
            val response = mangaDexApi.getTags()
            val tags = response.data.map { tagData ->
                tagData.attributes.name["en"] ?: ""
            }.filter { it.isNotEmpty() }
            Result.Success(tags)
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }
}