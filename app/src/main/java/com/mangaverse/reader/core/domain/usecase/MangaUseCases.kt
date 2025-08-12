package com.mangaverse.reader.core.domain.usecase

import androidx.paging.PagingData
import com.mangaverse.reader.core.data.repository.MangaRepository
import com.mangaverse.reader.core.domain.model.Manga
import com.mangaverse.reader.core.domain.model.MangaFilter
import com.mangaverse.reader.core.domain.model.MangaTag
import com.mangaverse.reader.core.domain.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Container for all manga-related use cases
 */
data class MangaUseCases(
    val getManga: GetMangaUseCase,
    val getMangaList: GetMangaListUseCase,
    val searchManga: SearchMangaUseCase,
    val getLibraryManga: GetLibraryMangaUseCase,
    val getFavoriteManga: GetFavoriteMangaUseCase,
    val getRecentlyReadManga: GetRecentlyReadMangaUseCase,
    val getMangaByGenre: GetMangaByGenreUseCase,
    val updateLibraryStatus: UpdateLibraryStatusUseCase,
    val updateFavoriteStatus: UpdateFavoriteStatusUseCase,
    val getMangaTags: GetMangaTagsUseCase
)

/**
 * Use case to get a manga by ID
 */
class GetMangaUseCase @Inject constructor(
    private val mangaRepository: MangaRepository
) {
    suspend operator fun invoke(id: String, forceRefresh: Boolean = false): Result<Manga> {
        return mangaRepository.getManga(id, forceRefresh)
    }
}

/**
 * Use case to get a paginated list of manga
 */
class GetMangaListUseCase @Inject constructor(
    private val mangaRepository: MangaRepository
) {
    operator fun invoke(filter: MangaFilter = MangaFilter()): Flow<PagingData<Manga>> {
        return mangaRepository.getMangaList(filter)
    }
}

/**
 * Use case to search for manga
 */
class SearchMangaUseCase @Inject constructor(
    private val mangaRepository: MangaRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Manga>> {
        return mangaRepository.searchManga(query)
    }
}

/**
 * Use case to get manga in the user's library
 */
class GetLibraryMangaUseCase @Inject constructor(
    private val mangaRepository: MangaRepository
) {
    operator fun invoke(): Flow<List<Manga>> {
        return mangaRepository.getLibraryManga()
    }
}

/**
 * Use case to get favorite manga
 */
class GetFavoriteMangaUseCase @Inject constructor(
    private val mangaRepository: MangaRepository
) {
    operator fun invoke(): Flow<List<Manga>> {
        return mangaRepository.getFavoriteManga()
    }
}

/**
 * Use case to get recently read manga
 */
class GetRecentlyReadMangaUseCase @Inject constructor(
    private val mangaRepository: MangaRepository
) {
    operator fun invoke(limit: Int = 10): Flow<List<Manga>> {
        return mangaRepository.getRecentlyReadManga(limit)
    }
}

/**
 * Use case to get manga by genre
 */
class GetMangaByGenreUseCase @Inject constructor(
    private val mangaRepository: MangaRepository
) {
    operator fun invoke(genre: String): Flow<List<Manga>> {
        return mangaRepository.getMangaByGenre(genre)
    }
}

/**
 * Use case to update a manga's library status
 */
class UpdateLibraryStatusUseCase @Inject constructor(
    private val mangaRepository: MangaRepository
) {
    suspend operator fun invoke(mangaId: String, inLibrary: Boolean): Result<Unit> {
        return mangaRepository.updateLibraryStatus(mangaId, inLibrary)
    }
}

/**
 * Use case to update a manga's favorite status
 */
class UpdateFavoriteStatusUseCase @Inject constructor(
    private val mangaRepository: MangaRepository
) {
    suspend operator fun invoke(mangaId: String, favorite: Boolean): Result<Unit> {
        return mangaRepository.updateFavoriteStatus(mangaId, favorite)
    }
}

/**
 * Use case to get all available manga tags
 */
class GetMangaTagsUseCase @Inject constructor(
    private val mangaRepository: MangaRepository
) {
    suspend operator fun invoke(): Result<List<MangaTag>> {
        return mangaRepository.getMangaTags()
    }
}