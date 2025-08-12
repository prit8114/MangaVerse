package com.mangaverse.reader.core.di

import com.mangaverse.reader.core.data.repository.*
import com.mangaverse.reader.core.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing use case dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideMangaUseCases(
        mangaRepository: MangaRepository
    ): MangaUseCases {
        return MangaUseCases(
            getManga = GetMangaUseCase(mangaRepository),
            getMangaList = GetMangaListUseCase(mangaRepository),
            searchManga = SearchMangaUseCase(mangaRepository),
            getLibraryManga = GetLibraryMangaUseCase(mangaRepository),
            getFavoriteManga = GetFavoriteMangaUseCase(mangaRepository),
            getRecentlyReadManga = GetRecentlyReadMangaUseCase(mangaRepository),
            getMangaByGenre = GetMangaByGenreUseCase(mangaRepository),
            updateLibraryStatus = UpdateLibraryStatusUseCase(mangaRepository),
            updateFavoriteStatus = UpdateFavoriteStatusUseCase(mangaRepository),
            getMangaTags = GetMangaTagsUseCase(mangaRepository)
        )
    }

    @Provides
    @Singleton
    fun provideChapterUseCases(
        chapterRepository: ChapterRepository
    ): ChapterUseCases {
        return ChapterUseCases(
            getChapter = GetChapterUseCase(chapterRepository),
            getChaptersForManga = GetChaptersForMangaUseCase(chapterRepository),
            getDownloadedChapters = GetDownloadedChaptersUseCase(chapterRepository),
            getBookmarkedChapters = GetBookmarkedChaptersUseCase(chapterRepository),
            getRecentlyReadChapters = GetRecentlyReadChaptersUseCase(chapterRepository),
            updateReadingProgress = UpdateReadingProgressUseCase(chapterRepository),
            toggleBookmarkStatus = ToggleBookmarkStatusUseCase(chapterRepository),
            getChapterCount = GetChapterCountUseCase(chapterRepository),
            getNextChapter = GetNextChapterUseCase(chapterRepository),
            getPreviousChapter = GetPreviousChapterUseCase(chapterRepository)
        )
    }

    @Provides
    @Singleton
    fun providePageUseCases(
        chapterRepository: ChapterRepository
    ): PageUseCases {
        return PageUseCases(
            getPages = GetPagesUseCase(chapterRepository),
            updatePageDetails = UpdatePageDetailsUseCase(chapterRepository)
        )
    }

    @Provides
    @Singleton
    fun provideDownloadUseCases(
        downloadManager: DownloadManager
    ): DownloadUseCases {
        return DownloadUseCases(
            queueDownload = QueueDownloadUseCase(downloadManager),
            queueMultipleDownloads = QueueMultipleDownloadsUseCase(downloadManager),
            cancelDownload = CancelDownloadUseCase(downloadManager),
            cancelAllDownloads = CancelAllDownloadsUseCase(downloadManager),
            pauseAllDownloads = PauseAllDownloadsUseCase(downloadManager),
            resumeAllDownloads = ResumeAllDownloadsUseCase(downloadManager),
            getDownloads = GetDownloadsUseCase(downloadManager),
            getDownloadsForManga = GetDownloadsForMangaUseCase(downloadManager),
            getActiveDownloads = GetActiveDownloadsUseCase(downloadManager),
            reorderDownload = ReorderDownloadUseCase(downloadManager)
        )
    }

    @Provides
    @Singleton
    fun provideReadingHistoryUseCases(
        readingHistoryRepository: ReadingHistoryRepository
    ): ReadingHistoryUseCases {
        return ReadingHistoryUseCases(
            getReadingHistory = GetReadingHistoryUseCase(readingHistoryRepository),
            getMangaReadingHistory = GetMangaReadingHistoryUseCase(readingHistoryRepository),
            getChapterReadingHistory = GetChapterReadingHistoryUseCase(readingHistoryRepository),
            addOrUpdateReadingHistory = AddOrUpdateReadingHistoryUseCase(readingHistoryRepository),
            deleteReadingHistory = DeleteReadingHistoryUseCase(readingHistoryRepository),
            deleteMangaReadingHistory = DeleteMangaReadingHistoryUseCase(readingHistoryRepository),
            deleteAllReadingHistory = DeleteAllReadingHistoryUseCase(readingHistoryRepository),
            syncReadingHistory = SyncReadingHistoryUseCase(readingHistoryRepository)
        )
    }

    @Provides
    @Singleton
    fun provideBookmarkUseCases(
        bookmarkRepository: BookmarkRepository
    ): BookmarkUseCases {
        return BookmarkUseCases(
            getBookmarks = GetBookmarksUseCase(bookmarkRepository),
            getMangaBookmarks = GetMangaBookmarksUseCase(bookmarkRepository),
            getChapterBookmarks = GetChapterBookmarksUseCase(bookmarkRepository),
            getBookmark = GetBookmarkUseCase(bookmarkRepository),
            addBookmark = AddBookmarkUseCase(bookmarkRepository),
            updateBookmark = UpdateBookmarkUseCase(bookmarkRepository),
            deleteBookmark = DeleteBookmarkUseCase(bookmarkRepository),
            deleteMangaBookmarks = DeleteMangaBookmarksUseCase(bookmarkRepository),
            deleteChapterBookmarks = DeleteChapterBookmarksUseCase(bookmarkRepository),
            isPageBookmarked = IsPageBookmarkedUseCase(bookmarkRepository),
            getPageBookmark = GetPageBookmarkUseCase(bookmarkRepository)
        )
    }

    @Provides
    @Singleton
    fun provideAuthUseCases(
        authRepository: AuthRepository
    ): AuthUseCases {
        return AuthUseCases(
            login = LoginUseCase(authRepository),
            logout = LogoutUseCase(authRepository),
            refreshToken = RefreshTokenUseCase(authRepository),
            isLoggedIn = IsLoggedInUseCase(authRepository),
            getCurrentUser = GetCurrentUserUseCase(authRepository)
        )
    }
}