package com.mangaverse.reader.core.domain.model

import java.util.Date

/**
 * Domain model representing a reading history entry
 *
 * @property id Unique identifier for the reading history entry
 * @property mangaId ID of the manga
 * @property chapterId ID of the chapter
 * @property pageNumber Last page number read
 * @property timestamp When the chapter was last read
 * @property sessionDuration Total time spent reading this chapter in milliseconds
 */
data class ReadingHistory(
    val id: String,
    val mangaId: String,
    val chapterId: String,
    val pageNumber: Int,
    val timestamp: Date,
    val sessionDuration: Long
)