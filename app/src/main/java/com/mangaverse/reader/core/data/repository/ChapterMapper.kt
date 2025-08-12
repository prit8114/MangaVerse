package com.mangaverse.reader.core.data.repository

import com.mangaverse.reader.core.data.database.entity.ChapterEntity
import com.mangaverse.reader.core.data.network.model.ChapterData
import com.mangaverse.reader.core.domain.model.Chapter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper for converting between domain models, database entities, and API responses for chapters
 */
@Singleton
class ChapterMapper @Inject constructor() {

    /**
     * Map API response to domain model
     */
    fun mapResponseToDomain(chapterData: ChapterData): Chapter {
        val attributes = chapterData.attributes
        
        // Extract manga ID from relationships
        val mangaRelationship = chapterData.relationships.find { it.type == "manga" }
        val mangaId = mangaRelationship?.id ?: ""
        
        // Extract scanlation group from relationships
        val groupRelationship = chapterData.relationships.find { it.type == "scanlation_group" }
        val groupId = groupRelationship?.id ?: ""
        
        // Parse chapter number
        val chapterNumber = attributes.chapter?.toFloatOrNull() ?: 0f
        
        return Chapter(
            id = chapterData.id,
            mangaId = mangaId,
            title = attributes.title,
            chapterNumber = chapterNumber,
            volume = attributes.volume,
            language = attributes.translatedLanguage,
            scanlationGroup = groupId, // Will need to be resolved later
            uploadDate = attributes.publishAt,
            pageCount = attributes.pages,
            downloaded = false, // Default value, will be updated from database
            downloadDate = null, // Default value, will be updated from database
            lastPageRead = 0, // Default value, will be updated from database
            readAt = null, // Default value, will be updated from database
            bookmarked = false // Default value, will be updated from database
        )
    }

    /**
     * Map domain model to database entity
     */
    fun mapDomainToEntity(chapter: Chapter): ChapterEntity {
        return ChapterEntity(
            id = chapter.id,
            mangaId = chapter.mangaId,
            title = chapter.title,
            chapterNumber = chapter.chapterNumber,
            volume = chapter.volume,
            language = chapter.language,
            scanlationGroup = chapter.scanlationGroup,
            uploadDate = chapter.uploadDate,
            pageCount = chapter.pageCount,
            downloaded = chapter.downloaded,
            downloadDate = chapter.downloadDate,
            lastPageRead = chapter.lastPageRead,
            readAt = chapter.readAt,
            bookmarked = chapter.bookmarked
        )
    }

    /**
     * Map database entity to domain model
     */
    fun mapEntityToDomain(chapterEntity: ChapterEntity): Chapter {
        return Chapter(
            id = chapterEntity.id,
            mangaId = chapterEntity.mangaId,
            title = chapterEntity.title,
            chapterNumber = chapterEntity.chapterNumber,
            volume = chapterEntity.volume,
            language = chapterEntity.language,
            scanlationGroup = chapterEntity.scanlationGroup,
            uploadDate = chapterEntity.uploadDate,
            pageCount = chapterEntity.pageCount,
            downloaded = chapterEntity.downloaded,
            downloadDate = chapterEntity.downloadDate,
            lastPageRead = chapterEntity.lastPageRead,
            readAt = chapterEntity.readAt,
            bookmarked = chapterEntity.bookmarked
        )
    }
}