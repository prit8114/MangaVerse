package com.mangaverse.reader.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.mangaverse.reader.core.data.database.dao.MangaDao
import com.mangaverse.reader.core.data.database.entity.MangaEntity
import com.mangaverse.reader.core.data.network.api.MangaDexApi
import com.mangaverse.reader.core.data.repository.MangaMapper
import com.mangaverse.reader.core.domain.model.MangaFilter
import com.mangaverse.reader.core.domain.model.OrderBy
import com.mangaverse.reader.core.domain.model.OrderDirection
import retrofit2.HttpException
import java.io.IOException

/**
 * RemoteMediator for loading manga data from the MangaDex API with paging
 */
@OptIn(ExperimentalPagingApi::class)
class MangaRemoteMediator(
    private val mangaDexApi: MangaDexApi,
    private val mangaDao: MangaDao,
    private val mangaMapper: MangaMapper,
    private val filter: MangaFilter
) : RemoteMediator<Int, MangaEntity>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 0
        private const val PAGE_SIZE = 20
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MangaEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                
                // Calculate the next page based on the number of items loaded
                val itemsLoaded = state.pages.sumOf { it.data.size }
                itemsLoaded / PAGE_SIZE
            }
        }

        try {
            // Convert filter to API parameters
            val orderMap = getOrderMap(filter.orderBy, filter.orderDirection)
            
            // Make API request
            val response = mangaDexApi.getMangaList(
                limit = state.config.pageSize,
                offset = page * state.config.pageSize,
                title = filter.title,
                authors = filter.authors,
                artists = filter.artists,
                year = filter.year,
                includedTags = filter.includedTags,
                excludedTags = filter.excludedTags,
                status = filter.status,
                originalLanguage = filter.originalLanguage,
                excludedOriginalLanguage = filter.excludedOriginalLanguage,
                availableTranslatedLanguage = filter.availableTranslatedLanguage,
                publicationDemographic = filter.publicationDemographic,
                ids = filter.ids,
                contentRating = filter.contentRating,
                orderRelevance = orderMap["relevance"],
                orderLatestUploadedChapter = orderMap["latestUploadedChapter"],
                orderTitle = orderMap["title"],
                orderRating = orderMap["rating"],
                orderFollowedCount = orderMap["followedCount"],
                orderCreatedAt = orderMap["createdAt"],
                orderUpdatedAt = orderMap["updatedAt"]
            )

            val mangaList = response.data.map { mangaData ->
                val manga = mangaMapper.mapResponseToDomain(mangaData)
                mangaMapper.mapDomainToEntity(manga)
            }

            // If refreshing, clear the database and insert new data
            if (loadType == LoadType.REFRESH) {
                // Don't clear the entire database, just update with new data
                // This preserves user preferences like inLibrary and favorite
            }

            // Insert all new manga
            mangaDao.insertAll(mangaList)

            return MediatorResult.Success(endOfPaginationReached = mangaList.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    /**
     * Convert OrderBy and OrderDirection to API parameters
     */
    private fun getOrderMap(orderBy: OrderBy, direction: OrderDirection): Map<String, String> {
        val directionStr = if (direction == OrderDirection.ASC) "asc" else "desc"
        val result = mutableMapOf<String, String?>()
        
        // Initialize all to null
        result["relevance"] = null
        result["latestUploadedChapter"] = null
        result["title"] = null
        result["rating"] = null
        result["followedCount"] = null
        result["createdAt"] = null
        result["updatedAt"] = null
        
        // Set the selected order
        when (orderBy) {
            OrderBy.RELEVANCE -> result["relevance"] = directionStr
            OrderBy.LATEST_CHAPTER -> result["latestUploadedChapter"] = directionStr
            OrderBy.TITLE -> result["title"] = directionStr
            OrderBy.RATING -> result["rating"] = directionStr
            OrderBy.FOLLOWED_COUNT -> result["followedCount"] = directionStr
            OrderBy.CREATED_AT -> result["createdAt"] = directionStr
            OrderBy.UPDATED_AT -> result["updatedAt"] = directionStr
        }
        
        return result.filterValues { it != null } as Map<String, String>
    }
}