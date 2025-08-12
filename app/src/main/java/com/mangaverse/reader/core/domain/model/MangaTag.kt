package com.mangaverse.reader.core.domain.model

/**
 * Domain model representing a manga tag
 *
 * @property id Unique identifier for the tag
 * @property name Name of the tag
 * @property group Group the tag belongs to (e.g., genre, theme, format)
 * @property description Optional description of the tag
 */
data class MangaTag(
    val id: String,
    val name: String,
    val group: String,
    val description: String? = null
)