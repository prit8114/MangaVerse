package com.mangaverse.reader.core.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mangaverse.reader.core.data.database.dao.*
import com.mangaverse.reader.core.data.database.entity.*
import com.mangaverse.reader.core.security.SecurityManager
import net.sqlcipher.database.SupportFactory

/**
 * Main database for the application
 */
@Database(
    entities = [
        MangaEntity::class,
        ChapterEntity::class,
        PageEntity::class,
        DownloadEntity::class,
        ReadingHistoryEntity::class,
        BookmarkEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(TypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mangaDao(): MangaDao
    abstract fun chapterDao(): ChapterDao
    abstract fun pageDao(): PageDao
    abstract fun downloadDao(): DownloadDao
    abstract fun readingHistoryDao(): ReadingHistoryDao
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        private const val DATABASE_NAME = "manga_reader.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, securityManager: SecurityManager): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context, securityManager)
                INSTANCE = instance
                instance
            }
        }

        private fun buildDatabase(context: Context, securityManager: SecurityManager): AppDatabase {
            val builder = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            
            // Apply encryption if available
            val encryptionKey = securityManager.getDatabaseEncryptionKey()
            if (encryptionKey != null) {
                val factory = SupportFactory(encryptionKey)
                builder.openHelperFactory(factory)
            }
            
            // Add migrations if needed
            // builder.addMigrations(MIGRATION_1_2)
            
            return builder
                .fallbackToDestructiveMigration()
                .build()
        }

        // Define migrations for future database schema changes
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Example migration code
                // database.execSQL("ALTER TABLE manga ADD COLUMN new_column INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}