package com.mangaverse.reader.core.domain.model

import java.util.Date

/**
 * Domain model for Manga
 */
data class Manga(
    val id: String,
    val title: String,
    val description: String,
    val author: String,
    val coverUrl: String,
    val status: String,
    val genres: List<String>,
    val tags: List<String>,
    val lastUpdated: Date,
    val inLibrary: Boolean,
    val favorite: Boolean,
    val lastRead: Date?,
    val downloadCount: Int
)