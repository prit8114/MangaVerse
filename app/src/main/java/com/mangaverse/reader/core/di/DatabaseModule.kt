package com.mangaverse.reader.core.di

import android.content.Context
import com.mangaverse.reader.core.data.database.AppDatabase
import com.mangaverse.reader.core.data.database.dao.*
import com.mangaverse.reader.core.security.SecurityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing database-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        securityManager: SecurityManager
    ): AppDatabase {
        return AppDatabase.getInstance(context, securityManager)
    }

    @Provides
    fun provideMangaDao(appDatabase: AppDatabase): MangaDao {
        return appDatabase.mangaDao()
    }

    @Provides
    fun provideChapterDao(appDatabase: AppDatabase): ChapterDao {
        return appDatabase.chapterDao()
    }

    @Provides
    fun providePageDao(appDatabase: AppDatabase): PageDao {
        return appDatabase.pageDao()
    }

    @Provides
    fun provideDownloadDao(appDatabase: AppDatabase): DownloadDao {
        return appDatabase.downloadDao()
    }

    @Provides
    fun provideReadingHistoryDao(appDatabase: AppDatabase): ReadingHistoryDao {
        return appDatabase.readingHistoryDao()
    }

    @Provides
    fun provideBookmarkDao(appDatabase: AppDatabase): BookmarkDao {
        return appDatabase.bookmarkDao()
    }
}