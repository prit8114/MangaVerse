package com.mangaverse.reader.core.domain.model

/**
 * Domain model for manga filtering options
 */
data class MangaFilter(
    val title: String? = null,
    val authors: List<String>? = null,
    val artists: List<String>? = null,
    val year: Int? = null,
    val includedTags: List<String>? = null,
    val excludedTags: List<String>? = null,
    val status: List<String>? = null,
    val originalLanguage: List<String>? = null,
    val excludedOriginalLanguage: List<String>? = null,
    val availableTranslatedLanguage: List<String>? = null,
    val publicationDemographic: List<String>? = null,
    val ids: List<String>? = null,
    val contentRating: List<String>? = null,
    val orderBy: OrderBy = OrderBy.RELEVANCE,
    val orderDirection: OrderDirection = OrderDirection.DESC
)

/**
 * Enum for manga ordering options
 */
enum class OrderBy {
    RELEVANCE,
    LATEST_CHAPTER,
    TITLE,
    RATING,
    FOLLOWED_COUNT,
    CREATED_AT,
    UPDATED_AT
}

/**
 * Enum for order direction
 */
enum class OrderDirection {
    ASC,
    DESC
}