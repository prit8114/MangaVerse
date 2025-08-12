package com.mangaverse.reader.core.data.database

import android.content.Context
import androidx.room.Room
import com.mangaverse.reader.core.security.SecurityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "mangaverse_db"

    @Provides
    @Singleton
    fun provideMangaDatabase(
        @ApplicationContext context: Context,
        securityManager: SecurityManager
    ): MangaDatabase {
        // Get encryption key from security manager
        val passphrase = securityManager.getDatabaseKey()
        
        // Create SQLCipher support factory with the encryption key
        val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase.toCharArray()))
        
        return Room.databaseBuilder(context, MangaDatabase::class.java, DATABASE_NAME)
            // Add SQLCipher support
            .openHelperFactory(factory)
            // Migration strategies
            .addMigrations(*MangaDatabase.MIGRATIONS)
            // Fallback to destructive migration if no migration path is found
            .fallbackToDestructiveMigration()
            // Allow main thread queries during development (remove for production)
            // .allowMainThreadQueries()
            .build()
    }

    @Provides
    fun provideMangaDao(database: MangaDatabase) = database.mangaDao()

    @Provides
    fun provideChapterDao(database: MangaDatabase) = database.chapterDao()

    @Provides
    fun providePageDao(database: MangaDatabase) = database.pageDao()
    
    @Provides
    fun provideDownloadDao(database: MangaDatabase) = database.downloadDao()
    
    @Provides
    fun provideReadingProgressDao(database: MangaDatabase) = database.readingProgressDao()
    
    @Provides
    fun provideSyncDao(database: MangaDatabase) = database.syncDao()
}