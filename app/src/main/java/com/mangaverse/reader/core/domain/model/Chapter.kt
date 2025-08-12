package com.mangaverse.reader.core.domain.model

import java.util.Date

/**
 * Domain model for Chapter
 */
data class Chapter(
    val id: String,
    val mangaId: String,
    val title: String?,
    val chapterNumber: Float,
    val volume: String?,
    val language: String,
    val scanlationGroup: String?,
    val uploadDate: Date,
    val pageCount: Int,
    val downloaded: Boolean,
    val downloadDate: Date?,
    val lastPageRead: Int,
    val readAt: Date?,
    val bookmarked: Boolean
)