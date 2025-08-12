package com.mangaverse.reader.core.domain.model

import java.util.Date

/**
 * Domain model representing a bookmark
 *
 * @property id Unique identifier for the bookmark
 * @property mangaId ID of the manga
 * @property chapterId ID of the chapter
 * @property pageNumber Page number of the bookmark
 * @property title Title of the bookmark
 * @property description Optional description of the bookmark
 * @property createdAt When the bookmark was created
 * @property thumbnailPath Optional path to a thumbnail image for the bookmark
 */
data class Bookmark(
    val id: String,
    val mangaId: String,
    val chapterId: String,
    val pageNumber: Int,
    val title: String,
    val description: String,
    val createdAt: Date,
    val thumbnailPath: String?
)