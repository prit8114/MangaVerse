package com.mangaverse.reader.core.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.mangaverse.reader.core.data.database.dao.MangaDao
import com.mangaverse.reader.core.data.database.entity.MangaEntity

@Database(
    entities = [MangaEntity::class],
    version = 1,
    exportSchema = true
)
abstract class MangaDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao

    companion object {
        const val DATABASE_NAME = "manga_database"
    }
}