package com.mangaverse.reader.core.domain.model

/**
 * Domain model for Page
 */
data class Page(
    val id: String,
    val chapterId: String,
    val pageNumber: Int,
    val imageUrl: String,
    val dataSaverUrl: String,
    val filePath: String?,
    val width: Int,
    val height: Int,
    val encryptionKey: String?,
    val detectedPanels: List<Panel>?
)

/**
 * Domain model for Panel (detected region in a page)
 */
data class Panel(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)