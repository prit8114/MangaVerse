package com.mangaverse.reader.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.mangaverse.reader.core.data.database.converters.DateConverter
import com.mangaverse.reader.core.data.database.converters.ListConverter
import com.mangaverse.reader.core.data.database.dao.*
import com.mangaverse.reader.core.data.database.entity.*

/**
 * Main database for the application.
 * Contains tables for manga, chapters, pages, and related data.
 */
@Database(
    entities = [
        MangaEntity::class,
        ChapterEntity::class,
        PageEntity::class,
        DownloadEntity::class,
        ReadingProgressEntity::class,
        SyncEntity::class,
        ReadingHistoryEntity::class,
        BookmarkEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class MangaDatabase : RoomDatabase() {

    /**
     * DAO for manga operations
     */
    abstract fun mangaDao(): MangaDao

    /**
     * DAO for chapter operations
     */
    abstract fun chapterDao(): ChapterDao

    /**
     * DAO for page operations
     */
    abstract fun pageDao(): PageDao
    
    /**
     * DAO for download operations
     */
    abstract fun downloadDao(): DownloadDao
    
    /**
     * DAO for reading progress operations
     */
    abstract fun readingProgressDao(): ReadingProgressDao
    
    /**
     * DAO for sync operations with external services
     */
    abstract fun syncDao(): SyncDao
    
    /**
     * DAO for reading history operations
     */
    abstract fun readingHistoryDao(): ReadingHistoryDao
    
    /**
     * DAO for bookmark operations
     */
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        // Define migrations here when needed
        val MIGRATIONS = arrayOf<Migration>(
            // Example migration from version 1 to 2
            // object : Migration(1, 2) {
            //     override fun migrate(database: SupportSQLiteDatabase) {
            //         // Migration implementation
            //     }
            // }
        )
    }
}