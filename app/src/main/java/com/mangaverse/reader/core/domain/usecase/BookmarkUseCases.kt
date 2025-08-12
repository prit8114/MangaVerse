package com.mangaverse.reader.core.domain.usecase

import com.mangaverse.reader.core.data.repository.BookmarkRepository
import com.mangaverse.reader.core.domain.model.Bookmark
import com.mangaverse.reader.core.domain.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Container for all bookmark-related use cases
 */
data class BookmarkUseCases(
    val getBookmarks: GetBookmarksUseCase,
    val getMangaBookmarks: GetMangaBookmarksUseCase,
    val getChapterBookmarks: GetChapterBookmarksUseCase,
    val getBookmark: GetBookmarkUseCase,
    val addBookmark: AddBookmarkUseCase,
    val updateBookmark: UpdateBookmarkUseCase,
    val deleteBookmark: DeleteBookmarkUseCase,
    val deleteMangaBookmarks: DeleteMangaBookmarksUseCase,
    val deleteChapterBookmarks: DeleteChapterBookmarksUseCase,
    val isPageBookmarked: IsPageBookmarkedUseCase,
    val getPageBookmark: GetPageBookmarkUseCase
)

/**
 * Use case to get all bookmarks
 */
class GetBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(): Flow<List<Bookmark>> {
        return bookmarkRepository.getBookmarksFlow()
    }
}

/**
 * Use case to get bookmarks for a manga
 */
class GetMangaBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(mangaId: String): Flow<List<Bookmark>> {
        return bookmarkRepository.getMangaBookmarksFlow(mangaId)
    }
}

/**
 * Use case to get bookmarks for a chapter
 */
class GetChapterBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    operator fun invoke(chapterId: String): Flow<List<Bookmark>> {
        return bookmarkRepository.getChapterBookmarksFlow(chapterId)
    }
}

/**
 * Use case to get a bookmark by ID
 */
class GetBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(id: String): Result<Bookmark?> {
        return try {
            val bookmark = bookmarkRepository.getBookmark(id)
            Result.Success(bookmark)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Use case to add a bookmark
 */
class AddBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(
        mangaId: String,
        chapterId: String,
        pageNumber: Int,
        title: String,
        description: String = "",
        thumbnailPath: String? = null
    ): Result<String> {
        return try {
            val id = bookmarkRepository.addBookmark(
                mangaId = mangaId,
                chapterId = chapterId,
                pageNumber = pageNumber,
                title = title,
                description = description,
                thumbnailPath = thumbnailPath
            )
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Use case to update a bookmark
 */
class UpdateBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(
        id: String,
        title: String? = null,
        description: String? = null,
        thumbnailPath: String? = null
    ): Result<Boolean> {
        return try {
            val success = bookmarkRepository.updateBookmark(
                id = id,
                title = title,
                description = description,
                thumbnailPath = thumbnailPath
            )
            Result.Success(success)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Use case to delete a bookmark
 */
class DeleteBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return try {
            bookmarkRepository.deleteBookmark(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Use case to delete all bookmarks for a manga
 */
class DeleteMangaBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(mangaId: String): Result<Unit> {
        return try {
            bookmarkRepository.deleteMangaBookmarks(mangaId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Use case to delete all bookmarks for a chapter
 */
class DeleteChapterBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(chapterId: String): Result<Unit> {
        return try {
            bookmarkRepository.deleteChapterBookmarks(chapterId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Use case to check if a page is bookmarked
 */
class IsPageBookmarkedUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(chapterId: String, pageNumber: Int): Result<Boolean> {
        return try {
            val isBookmarked = bookmarkRepository.isPageBookmarked(chapterId, pageNumber)
            Result.Success(isBookmarked)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Use case to get a bookmark for a specific page
 */
class GetPageBookmarkUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(chapterId: String, pageNumber: Int): Result<Bookmark?> {
        return try {
            val bookmark = bookmarkRepository.getPageBookmark(chapterId, pageNumber)
            Result.Success(bookmark)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}