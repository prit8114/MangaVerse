package com.mangaverse.reader.core.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing reading history entries
 */
@Entity(
    tableName = "reading_history",
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["id"],
            childColumns = ["mangaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("mangaId"),
        Index("chapterId"),
        Index("timestamp")
    ]
)
data class ReadingHistoryEntity(
    @PrimaryKey
    val id: String,
    val mangaId: String,
    val chapterId: String,
    val timestamp: Date,
    val pageNumber: Int,
    val sessionDuration: Long // in seconds
)