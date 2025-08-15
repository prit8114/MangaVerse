package com.mangaverse.reader.core.di

import android.content.Context
import androidx.room.Room
import com.mangaverse.reader.core.data.database.MangaDatabase
import com.mangaverse.reader.core.data.database.dao.MangaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMangaDatabase(
        @ApplicationContext context: Context
    ): MangaDatabase {
        return Room.databaseBuilder(
            context,
            MangaDatabase::class.java,
            MangaDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMangaDao(database: MangaDatabase): MangaDao = database.mangaDao()
}