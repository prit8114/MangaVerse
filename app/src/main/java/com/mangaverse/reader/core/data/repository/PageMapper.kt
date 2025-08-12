package com.mangaverse.reader.core.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mangaverse.reader.core.data.database.entity.PageEntity
import com.mangaverse.reader.core.domain.model.Page
import com.mangaverse.reader.core.domain.model.Panel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper for converting between domain models and database entities for pages
 */
@Singleton
class PageMapper @Inject constructor(
    private val gson: Gson
) {

    /**
     * Map database entity to domain model
     */
    fun mapEntityToDomain(pageEntity: PageEntity): Page {
        // Parse detected panels JSON if available
        val detectedPanels = if (pageEntity.detectedPanels != null) {
            val type = object : TypeToken<List<Panel>>() {}.type
            gson.fromJson<List<Panel>>(pageEntity.detectedPanels, type)
        } else {
            null
        }
        
        return Page(
            id = pageEntity.id,
            chapterId = pageEntity.chapterId,
            pageNumber = pageEntity.pageNumber,
            imageUrl = pageEntity.imageUrl,
            dataSaverUrl = pageEntity.dataSaverUrl ?: "",
            filePath = pageEntity.filePath,
            width = pageEntity.width,
            height = pageEntity.height,
            encryptionKey = pageEntity.encryptionKey,
            detectedPanels = detectedPanels
        )
    }

    /**
     * Map domain model to database entity
     */
    fun mapDomainToEntity(page: Page): PageEntity {
        // Convert detected panels to JSON if available
        val detectedPanelsJson = if (page.detectedPanels != null) {
            gson.toJson(page.detectedPanels)
        } else {
            null
        }
        
        return PageEntity(
            id = page.id,
            chapterId = page.chapterId,
            pageNumber = page.pageNumber,
            imageUrl = page.imageUrl,
            dataSaverUrl = page.dataSaverUrl,
            filePath = page.filePath,
            width = page.width,
            height = page.height,
            encryptionKey = page.encryptionKey,
            detectedPanels = detectedPanelsJson
        )
    }
}