package com.mangaverse.reader.core.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing bookmarks in chapters
 */
@Entity(
    tableName = "bookmarks",
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
        Index("pageNumber")
    ]
)
data class BookmarkEntity(
    @PrimaryKey
    val id: String,
    val chapterId: String,
    val mangaId: String,
    val pageNumber: Int,
    val title: String?,
    val description: String?,
    val createdAt: Date,
    val thumbnailPath: String?
)