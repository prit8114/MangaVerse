package com.mangaverse.reader.core.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a download task in the database
 */
@Entity(
    tableName = "downloads",
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
        Index("status"),
        Index("queuePosition")
    ]
)
data class DownloadEntity(
    @PrimaryKey
    val id: String,
    val chapterId: String,
    val mangaId: String,
    val status: DownloadStatus,
    val progress: Int, // 0-100
    val totalPages: Int,
    val downloadedPages: Int,
    val queuePosition: Int,
    val dateAdded: Date,
    val dateUpdated: Date
)

enum class DownloadStatus {
    QUEUED,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    ERROR
}