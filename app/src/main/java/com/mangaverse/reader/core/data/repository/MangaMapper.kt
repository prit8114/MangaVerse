package com.mangaverse.reader.core.data.repository

import com.mangaverse.reader.core.data.database.entity.MangaEntity
import com.mangaverse.reader.core.data.network.model.MangaData
import com.mangaverse.reader.core.domain.model.Manga
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper for converting between domain models, database entities, and API responses
 */
@Singleton
class MangaMapper @Inject constructor() {

    /**
     * Map API response to domain model
     */
    fun mapResponseToDomain(mangaData: MangaData): Manga {
        val attributes = mangaData.attributes
        val title = attributes.title["en"] ?: attributes.title.values.firstOrNull() ?: ""
        
        // Extract cover URL from relationships
        val coverRelationship = mangaData.relationships.find { it.type == "cover_art" }
        val coverId = coverRelationship?.id
        val coverUrl = if (coverId != null) {
            "https://uploads.mangadex.org/covers/${mangaData.id}/$coverId.jpg"
        } else {
            ""
        }
        
        // Extract author from relationships
        val authorRelationship = mangaData.relationships.find { it.type == "author" }
        val authorId = authorRelationship?.id ?: ""
        
        // Extract genres from tags
        val genres = attributes.tags
            .filter { it.attributes.group == "genre" }
            .map { it.attributes.name["en"] ?: "" }
            .filter { it.isNotEmpty() }
        
        // Extract themes from tags
        val themes = attributes.tags
            .filter { it.attributes.group == "theme" }
            .map { it.attributes.name["en"] ?: "" }
            .filter { it.isNotEmpty() }
        
        return Manga(
            id = mangaData.id,
            title = title,
            description = attributes.description["en"] ?: "",
            author = authorId, // Will need to be resolved later
            coverUrl = coverUrl,
            status = attributes.status,
            genres = genres,
            tags = themes,
            lastUpdated = attributes.updatedAt,
            inLibrary = false, // Default value, will be updated from database
            favorite = false, // Default value, will be updated from database
            lastRead = null, // Default value, will be updated from database
            downloadCount = 0 // Default value, will be updated from database
        )
    }

    /**
     * Map domain model to database entity
     */
    fun mapDomainToEntity(manga: Manga): MangaEntity {
        return MangaEntity(
            id = manga.id,
            title = manga.title,
            description = manga.description,
            author = manga.author,
            coverUrl = manga.coverUrl,
            status = manga.status,
            genres = manga.genres,
            tags = manga.tags,
            lastUpdated = manga.lastUpdated,
            inLibrary = manga.inLibrary,
            favorite = manga.favorite,
            lastRead = manga.lastRead,
            downloadCount = manga.downloadCount
        )
    }

    /**
     * Map database entity to domain model
     */
    fun mapEntityToDomain(mangaEntity: MangaEntity): Manga {
        return Manga(
            id = mangaEntity.id,
            title = mangaEntity.title,
            description = mangaEntity.description,
            author = mangaEntity.author,
            coverUrl = mangaEntity.coverUrl,
            status = mangaEntity.status,
            genres = mangaEntity.genres,
            tags = mangaEntity.tags,
            lastUpdated = mangaEntity.lastUpdated,
            inLibrary = mangaEntity.inLibrary,
            favorite = mangaEntity.favorite,
            lastRead = mangaEntity.lastRead,
            downloadCount = mangaEntity.downloadCount
        )
    }
}