package com.mangaverse.reader.core.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing reading progress for a chapter
 */
@Entity(
    tableName = "reading_progress",
    foreignKeys = [
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("chapterId"),
        Index("mangaId"),
        Index("lastReadAt")
    ]
)
data class ReadingProgressEntity(
    @PrimaryKey
    val id: String,
    val chapterId: String,
    val mangaId: String,
    val currentPage: Int,
    val totalPages: Int,
    val completed: Boolean,
    val lastReadAt: Date,
    val readDuration: Long, // in seconds
    val syncedWithMal: Boolean,
    val syncedWithAniList: Boolean
)