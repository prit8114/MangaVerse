package com.mangaverse.reader.core.di

import com.mangaverse.reader.core.data.database.dao.*
import com.mangaverse.reader.core.data.network.api.MangaDexApi
import com.mangaverse.reader.core.data.preferences.PreferencesManager
import com.mangaverse.reader.core.data.repository.*
import com.mangaverse.reader.core.security.FileEncryptionService
import com.mangaverse.reader.core.security.SecurityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing repository-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        mangaDexApi: MangaDexApi,
        securityManager: SecurityManager,
        preferencesManager: PreferencesManager
    ): AuthRepository {
        return AuthRepository(mangaDexApi, securityManager)
        // Note: The AuthRepository implementation currently doesn't use PreferencesManager
        // but we're injecting it for future use when the implementation is updated
    }

    @Provides
    @Singleton
    fun provideMangaRepository(
        mangaDao: MangaDao,
        mangaDexApi: MangaDexApi
    ): MangaRepository {
        return MangaRepository(mangaDao, mangaDexApi)
    }

    @Provides
    @Singleton
    fun provideChapterRepository(
        chapterDao: ChapterDao,
        pageDao: PageDao,
        mangaDexApi: MangaDexApi
    ): ChapterRepository {
        return ChapterRepository(chapterDao, pageDao, mangaDexApi)
    }

    @Provides
    @Singleton
    fun provideDownloadManager(
        downloadDao: DownloadDao,
        mangaDao: MangaDao,
        chapterDao: ChapterDao,
        pageDao: PageDao,
        chapterRepository: ChapterRepository,
        fileEncryptionService: FileEncryptionService,
        @Named("downloadClient") okHttpClient: OkHttpClient
    ): DownloadManager {
        return DownloadManager(
            downloadDao,
            mangaDao,
            chapterDao,
            pageDao,
            chapterRepository,
            fileEncryptionService,
            okHttpClient
        )
    }

    @Provides
    @Singleton
    fun provideReadingHistoryRepository(
        readingHistoryDao: ReadingHistoryDao,
        mangaDexApi: MangaDexApi,
        authRepository: AuthRepository
    ): ReadingHistoryRepository {
        return ReadingHistoryRepository(readingHistoryDao, mangaDexApi, authRepository)
    }

    @Provides
    @Singleton
    fun provideBookmarkRepository(
        bookmarkDao: BookmarkDao
    ): BookmarkRepository {
        return BookmarkRepository(bookmarkDao)
    }
}