package com.mangaverse.reader.core.domain.usecase

import androidx.paging.PagingData
import com.mangaverse.reader.core.data.repository.ChapterRepository
import com.mangaverse.reader.core.domain.model.Chapter
import com.mangaverse.reader.core.domain.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Container for all chapter-related use cases
 */
data class ChapterUseCases(
    val getChapter: GetChapterUseCase,
    val getChaptersForManga: GetChaptersForMangaUseCase,
    val getDownloadedChapters: GetDownloadedChaptersUseCase,
    val getBookmarkedChapters: GetBookmarkedChaptersUseCase,
    val getRecentlyReadChapters: GetRecentlyReadChaptersUseCase,
    val updateReadingProgress: UpdateReadingProgressUseCase,
    val toggleBookmarkStatus: ToggleBookmarkStatusUseCase,
    val getChapterCount: GetChapterCountUseCase,
    val getNextChapter: GetNextChapterUseCase,
    val getPreviousChapter: GetPreviousChapterUseCase
)

/**
 * Use case to get a chapter by ID
 */
class GetChapterUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository
) {
    suspend operator fun invoke(id: String, forceRefresh: Boolean = false): Result<Chapter> {
        return chapterRepository.getChapter(id, forceRefresh)
    }
}

/**
 * Use case to get chapters for a manga
 */
class GetChaptersForMangaUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository
) {
    operator fun invoke(mangaId: String, usePagedData: Boolean = false): Flow<List<Chapter>> {
        return chapterRepository.getChaptersForManga(mangaId)
    }

    fun getPaged(mangaId: String): Flow<PagingData<Chapter>> {
        return chapterRepository.getChaptersForMangaPaged(mangaId)
    }
}

/**
 * Use case to get downloaded chapters
 */
class GetDownloadedChaptersUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository
) {
    operator fun invoke(mangaId: String? = null): Flow<List<Chapter>> {
        return if (mangaId != null) {
            chapterRepository.getDownloadedChaptersForManga(mangaId)
        } else {
            chapterRepository.getDownloadedChapters()
        }
    }
}

/**
 * Use case to get bookmarked chapters
 */
class GetBookmarkedChaptersUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository
) {
    operator fun invoke(mangaId: String? = null): Flow<List<Chapter>> {
        return if (mangaId != null) {
            chapterRepository.getBookmarkedChaptersForManga(mangaId)
        } else {
            chapterRepository.getBookmarkedChapters()
        }
    }
}

/**
 * Use case to get recently read chapters
 */
class GetRecentlyReadChaptersUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository
) {
    operator fun invoke(limit: Int = 20): Flow<List<Chapter>> {
        return chapterRepository.getRecentlyReadChapters(limit)
    }
}

/**
 * Use case to update reading progress for a chapter
 */
class UpdateReadingProgressUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository
) {
    suspend operator fun invoke(chapterId: String, lastPageRead: Int): Result<Unit> {
        return chapterRepository.updateReadingProgress(chapterId, lastPageRead)
    }
}

/**
 * Use case to toggle bookmark status for a chapter
 */
class ToggleBookmarkStatusUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository
) {
    suspend operator fun invoke(chapterId: String, bookmarked: Boolean): Result<Unit> {
        return chapterRepository.toggleBookmarkStatus(chapterId, bookmarked)
    }
}

/**
 * Use case to get chapter counts for a manga
 */
class GetChapterCountUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository
) {
    suspend operator fun invoke(mangaId: String): Result<Triple<Int, Int, Int>> {
        return chapterRepository.getChapterCounts(mangaId)
    }
}

/**
 * Use case to get the next chapter
 */
class GetNextChapterUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository
) {
    suspend operator fun invoke(currentChapterId: String): Result<Chapter?> {
        return chapterRepository.getNextChapter(currentChapterId)
    }
}

/**
 * Use case to get the previous chapter
 */
class GetPreviousChapterUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository
) {
    suspend operator fun invoke(currentChapterId: String): Result<Chapter?> {
        return chapterRepository.getPreviousChapter(currentChapterId)
    }
}