package com.mangaverse.reader.core.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga")
data class MangaEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String? = null,
    val coverUrl: String? = null,
    val status: String? = null,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)