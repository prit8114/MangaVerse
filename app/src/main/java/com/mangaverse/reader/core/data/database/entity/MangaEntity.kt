package com.mangaverse.reader.core.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a manga in the database
 */
@Entity(
    tableName = "manga",
    indices = [
        Index("title"),
        Index("author"),
        Index("status"),
        Index("lastUpdated")
    ]
)
data class MangaEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val altTitles: List<String>,
    val description: String,
    val author: String,
    val artist: String,
    val coverUrl: String,
    val thumbnailUrl: String,
    val status: String, // ongoing, completed, hiatus, cancelled
    val genres: List<String>,
    val tags: List<String>,
    val contentRating: String, // safe, suggestive, erotica, pornographic
    val originalLanguage: String,
    val publicationDemographic: String?, // shounen, shoujo, seinen, josei
    val year: Int?,
    val lastUpdated: Date,
    val lastRead: Date?,
    val inLibrary: Boolean,
    val favorite: Boolean,
    val downloadCount: Int,
    val chapterCount: Int,
    val source: String, // mangadex, etc.
    val sourceUrl: String
)