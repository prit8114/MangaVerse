package com.mangaverse.reader.core.data.network.api

import com.mangaverse.reader.core.data.network.model.*
import retrofit2.http.*

/**
 * Retrofit interface for MangaDex API
 */
interface MangaDexApi {

    companion object {
        private const val BASE_URL = "https://api.mangadex.org"
        private const val DEFAULT_LIMIT = 20
    }

    /**
     * Get manga list with filters
     */
    @GET("manga")
    suspend fun getMangaList(
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("offset") offset: Int = 0,
        @Query("title") title: String? = null,
        @Query("authors[]") authors: List<String>? = null,
        @Query("artists[]") artists: List<String>? = null,
        @Query("year") year: Int? = null,
        @Query("includedTags[]") includedTags: List<String>? = null,
        @Query("excludedTags[]") excludedTags: List<String>? = null,
        @Query("status[]") status: List<String>? = null,
        @Query("originalLanguage[]") originalLanguage: List<String>? = null,
        @Query("excludedOriginalLanguage[]") excludedOriginalLanguage: List<String>? = null,
        @Query("availableTranslatedLanguage[]") availableTranslatedLanguage: List<String>? = null,
        @Query("publicationDemographic[]") publicationDemographic: List<String>? = null,
        @Query("ids[]") ids: List<String>? = null,
        @Query("contentRating[]") contentRating: List<String>? = null,
        @Query("createdAtSince") createdAtSince: String? = null,
        @Query("updatedAtSince") updatedAtSince: String? = null,
        @Query("order[relevance]") orderRelevance: String? = null,
        @Query("order[latestUploadedChapter]") orderLatestUploadedChapter: String? = null,
        @Query("order[title]") orderTitle: String? = null,
        @Query("order[rating]") orderRating: String? = null,
        @Query("order[followedCount]") orderFollowedCount: String? = null,
        @Query("order[createdAt]") orderCreatedAt: String? = null,
        @Query("order[updatedAt]") orderUpdatedAt: String? = null,
        @Query("includes[]") includes: List<String> = listOf("cover_art", "author", "artist")
    ): MangaListResponse

    /**
     * Get manga by ID
     */
    @GET("manga/{id}")
    suspend fun getManga(
        @Path("id") id: String,
        @Query("includes[]") includes: List<String> = listOf("cover_art", "author", "artist")
    ): MangaResponse

    /**
     * Get chapters for a manga
     */
    @GET("manga/{id}/feed")
    suspend fun getMangaFeed(
        @Path("id") id: String,
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("offset") offset: Int = 0,
        @Query("translatedLanguage[]") translatedLanguage: List<String>? = null,
        @Query("order[volume]") orderVolume: String? = null,
        @Query("order[chapter]") orderChapter: String? = null,
        @Query("includes[]") includes: List<String> = listOf("scanlation_group")
    ): ChapterListResponse

    /**
     * Get chapter by ID
     */
    @GET("chapter/{id}")
    suspend fun getChapter(
        @Path("id") id: String,
        @Query("includes[]") includes: List<String> = listOf("scanlation_group")
    ): ChapterResponse

    /**
     * Get chapter pages
     */
    @GET("at-home/server/{id}")
    suspend fun getChapterPages(
        @Path("id") id: String,
        @Query("forcePort443") forcePort443: Boolean = false
    ): AtHomeResponse

    /**
     * Search manga
     */
    @GET("manga")
    suspend fun searchManga(
        @Query("title") title: String,
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("offset") offset: Int = 0,
        @Query("includes[]") includes: List<String> = listOf("cover_art", "author", "artist")
    ): MangaListResponse

    /**
     * Get manga tags
     */
    @GET("manga/tag")
    suspend fun getTags(): TagsResponse

    /**
     * Get manga statistics
     */
    @GET("statistics/manga/{id}")
    suspend fun getMangaStatistics(
        @Path("id") id: String
    ): StatisticsResponse

    /**
     * Get manga covers
     */
    @GET("cover")
    suspend fun getCovers(
        @Query("manga[]") mangaIds: List<String>,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): CoverListResponse

    /**
     * Get author information
     */
    @GET("author/{id}")
    suspend fun getAuthor(
        @Path("id") id: String
    ): AuthorResponse

    /**
     * Get scanlation group information
     */
    @GET("group/{id}")
    suspend fun getScanlationGroup(
        @Path("id") id: String
    ): ScanlationGroupResponse

    /**
     * Login to MangaDex
     */
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    /**
     * Refresh authentication token
     */
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body refreshRequest: RefreshTokenRequest
    ): RefreshTokenResponse

    /**
     * Logout from MangaDex
     */
    @POST("auth/logout")
    suspend fun logout(
        @Body logoutRequest: LogoutRequest
    ): LogoutResponse

    /**
     * Get user's manga reading list
     */
    @GET("user/follows/manga")
    suspend fun getUserFollowedManga(
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("offset") offset: Int = 0,
        @Query("includes[]") includes: List<String> = listOf("cover_art")
    ): MangaListResponse

    /**
     * Follow a manga
     */
    @POST("manga/{id}/follow")
    suspend fun followManga(
        @Path("id") id: String
    ): FollowResponse

    /**
     * Unfollow a manga
     */
    @DELETE("manga/{id}/follow")
    suspend fun unfollowManga(
        @Path("id") id: String
    ): FollowResponse

    /**
     * Get user's reading status for manga
     */
    @GET("manga/{id}/status")
    suspend fun getMangaReadingStatus(
        @Path("id") id: String
    ): ReadingStatusResponse

    /**
     * Update reading status for manga
     */
    @POST("manga/{id}/status")
    suspend fun updateMangaReadingStatus(
        @Path("id") id: String,
        @Body statusRequest: ReadingStatusUpdateRequest
    ): ReadingStatusResponse

    /**
     * Get user's reading history
     */
    @GET("user/history")
    suspend fun getReadingHistory(
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("offset") offset: Int = 0
    ): ReadingHistoryResponse

    /**
     * Mark chapter as read
     */
    @POST("chapter/{id}/read")
    suspend fun markChapterRead(
        @Path("id") id: String
    ): MarkReadResponse

    /**
     * Mark chapter as unread
     */
    @DELETE("chapter/{id}/read")
    suspend fun markChapterUnread(
        @Path("id") id: String
    ): MarkReadResponse
}