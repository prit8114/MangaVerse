package com.mangaverse.reader.core.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a manga page in the database
 */
@Entity(
    tableName = "pages",
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
        Index("pageNumber")
    ]
)
data class PageEntity(
    @PrimaryKey
    val id: String,
    val chapterId: String,
    val pageNumber: Int,
    val imageUrl: String,
    val filePath: String?, // Local file path for downloaded pages
    val width: Int?,
    val height: Int?,
    val encryptionKey: String?, // Encryption key for the local file
    val detectedPanels: String? // JSON string of detected panel coordinates
)