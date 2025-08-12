package com.mangaverse.reader.core.data.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Base response for MangaDex API
 */
data class MangaDexResponse<T>(
    val result: String,
    val response: String,
    val data: T,
    val limit: Int? = null,
    val offset: Int? = null,
    val total: Int? = null
)

/**
 * Manga list response
 */
data class MangaListResponse(
    val result: String,
    val response: String,
    val data: List<MangaData>,
    val limit: Int,
    val offset: Int,
    val total: Int
)

/**
 * Single manga response
 */
data class MangaResponse(
    val result: String,
    val response: String,
    val data: MangaData
)

/**
 * Manga data
 */
data class MangaData(
    val id: String,
    val type: String,
    val attributes: MangaAttributes,
    val relationships: List<Relationship>
)

/**
 * Manga attributes
 */
data class MangaAttributes(
    val title: Map<String, String>,
    val altTitles: List<Map<String, String>>,
    val description: Map<String, String>,
    val isLocked: Boolean,
    val links: Map<String, String>?,
    val originalLanguage: String,
    val lastVolume: String?,
    val lastChapter: String?,
    val publicationDemographic: String?,
    val status: String,
    val year: Int?,
    val contentRating: String,
    val tags: List<TagData>,
    val state: String,
    val chapterNumbersResetOnNewVolume: Boolean,
    val createdAt: Date,
    val updatedAt: Date,
    val version: Int,
    val availableTranslatedLanguages: List<String>
)

/**
 * Tag data
 */
data class TagData(
    val id: String,
    val type: String,
    val attributes: TagAttributes
)

/**
 * Tag attributes
 */
data class TagAttributes(
    val name: Map<String, String>,
    val description: Map<String, String>?,
    val group: String,
    val version: Int
)

/**
 * Chapter list response
 */
data class ChapterListResponse(
    val result: String,
    val response: String,
    val data: List<ChapterData>,
    val limit: Int,
    val offset: Int,
    val total: Int
)

/**
 * Single chapter response
 */
data class ChapterResponse(
    val result: String,
    val response: String,
    val data: ChapterData
)

/**
 * Chapter data
 */
data class ChapterData(
    val id: String,
    val type: String,
    val attributes: ChapterAttributes,
    val relationships: List<Relationship>
)

/**
 * Chapter attributes
 */
data class ChapterAttributes(
    val title: String?,
    val volume: String?,
    val chapter: String?,
    val pages: Int,
    val translatedLanguage: String,
    val uploader: String?,
    val externalUrl: String?,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date,
    val publishAt: Date,
    val readableAt: Date
)

/**
 * Relationship data
 */
data class Relationship(
    val id: String,
    val type: String,
    val attributes: Any? = null
)

/**
 * At-home server response for chapter pages
 */
data class AtHomeResponse(
    val result: String,
    val baseUrl: String,
    val chapter: AtHomeChapter
)

/**
 * At-home chapter data
 */
data class AtHomeChapter(
    val hash: String,
    val data: List<String>,
    val dataSaver: List<String>
)

/**
 * Tags response
 */
data class TagsResponse(
    val result: String,
    val response: String,
    val data: List<TagData>
)

/**
 * Statistics response
 */
data class StatisticsResponse(
    val result: String,
    val statistics: Map<String, MangaStatistics>
)

/**
 * Manga statistics
 */
data class MangaStatistics(
    val rating: RatingStatistics,
    val follows: Int
)

/**
 * Rating statistics
 */
data class RatingStatistics(
    val average: Float?,
    val bayesian: Float?,
    val distribution: Map<String, Int>
)

/**
 * Cover list response
 */
data class CoverListResponse(
    val result: String,
    val response: String,
    val data: List<CoverData>,
    val limit: Int,
    val offset: Int,
    val total: Int
)

/**
 * Cover data
 */
data class CoverData(
    val id: String,
    val type: String,
    val attributes: CoverAttributes,
    val relationships: List<Relationship>
)

/**
 * Cover attributes
 */
data class CoverAttributes(
    val volume: String?,
    val fileName: String,
    val description: String?,
    val locale: String?,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date
)

/**
 * Author response
 */
data class AuthorResponse(
    val result: String,
    val response: String,
    val data: AuthorData
)

/**
 * Author data
 */
data class AuthorData(
    val id: String,
    val type: String,
    val attributes: AuthorAttributes
)

/**
 * Author attributes
 */
data class AuthorAttributes(
    val name: String,
    val imageUrl: String?,
    val biography: Map<String, String>?,
    val twitter: String?,
    val pixiv: String?,
    val melonBook: String?,
    val fanBox: String?,
    val booth: String?,
    val nicoVideo: String?,
    val skeb: String?,
    val fantia: String?,
    val tumblr: String?,
    val youtube: String?,
    val weibo: String?,
    val naver: String?,
    val website: String?,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date
)

/**
 * Scanlation group response
 */
data class ScanlationGroupResponse(
    val result: String,
    val response: String,
    val data: ScanlationGroupData
)

/**
 * Scanlation group data
 */
data class ScanlationGroupData(
    val id: String,
    val type: String,
    val attributes: ScanlationGroupAttributes
)

/**
 * Scanlation group attributes
 */
data class ScanlationGroupAttributes(
    val name: String,
    val altNames: List<Map<String, String>>,
    val website: String?,
    val ircServer: String?,
    val ircChannel: String?,
    val discord: String?,
    val contactEmail: String?,
    val description: String?,
    val twitter: String?,
    val mangaUpdates: String?,
    val focusedLanguages: List<String>?,
    val locked: Boolean,
    val official: Boolean,
    val inactive: Boolean,
    val publishDelay: String?,
    val version: Int,
    val createdAt: Date,
    val updatedAt: Date
)

/**
 * Login request
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * Login response
 */
data class LoginResponse(
    val result: String,
    val token: TokenData
)

/**
 * Token data
 */
data class TokenData(
    val session: String,
    val refresh: String
)

/**
 * Refresh token request
 */
data class RefreshTokenRequest(
    val token: String
)

/**
 * Refresh token response
 */
data class RefreshTokenResponse(
    val result: String,
    val token: TokenData
)

/**
 * Logout request
 */
data class LogoutRequest(
    val token: String
)

/**
 * Logout response
 */
data class LogoutResponse(
    val result: String
)

/**
 * Follow response
 */
data class FollowResponse(
    val result: String
)

/**
 * Reading status response
 */
data class ReadingStatusResponse(
    val result: String,
    val status: String?
)

/**
 * Reading status update request
 */
data class ReadingStatusUpdateRequest(
    val status: String
)

/**
 * Reading history response
 */
data class ReadingHistoryResponse(
    val result: String,
    val response: String,
    val data: List<ReadingHistoryData>,
    val limit: Int,
    val offset: Int,
    val total: Int
)

/**
 * Reading history data
 */
data class ReadingHistoryData(
    val chapterId: String,
    val readAt: Date
)

/**
 * Mark read response
 */
data class MarkReadResponse(
    val result: String
)