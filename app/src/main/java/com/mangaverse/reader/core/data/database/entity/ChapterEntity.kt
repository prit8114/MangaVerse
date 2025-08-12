package com.mangaverse.reader.core.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a manga chapter in the database
 */
@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["id"],
            childColumns = ["mangaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("mangaId"),
        Index("chapterNumber"),
        Index("uploadDate"),
        Index("downloaded")
    ]
)
data class ChapterEntity(
    @PrimaryKey
    val id: String,
    val mangaId: String,
    val title: String?,
    val chapterNumber: Float,
    val volume: Int?,
    val scanlationGroup: String?,
    val language: String,
    val uploadDate: Date,
    val pageCount: Int,
    val downloaded: Boolean,
    val downloadDate: Date?,
    val readAt: Date?,
    val lastPageRead: Int,
    val bookmarked: Boolean,
    val sourceUrl: String
)