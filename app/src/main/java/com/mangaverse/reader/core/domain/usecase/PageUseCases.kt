package com.mangaverse.reader.core.domain.usecase

import com.mangaverse.reader.core.data.repository.ChapterRepository
import com.mangaverse.reader.core.domain.model.Page
import com.mangaverse.reader.core.domain.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Container for all page-related use cases
 */
data class PageUseCases(
    val getPages: GetPagesUseCase,
    val updatePageDetails: UpdatePageDetailsUseCase
)

/**
 * Use case to get pages for a chapter
 */
class GetPagesUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository
) {
    operator fun invoke(chapterId: String, forceRefresh: Boolean = false): Flow<List<Page>> {
        return chapterRepository.getPagesForChapter(chapterId, forceRefresh)
    }

    suspend fun getSync(chapterId: String, forceRefresh: Boolean = false): Result<List<Page>> {
        return chapterRepository.getPagesForChapterSync(chapterId, forceRefresh)
    }
}

/**
 * Use case to update page details
 */
class UpdatePageDetailsUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository
) {
    suspend operator fun invoke(
        pageId: String,
        filePath: String? = null,
        width: Int? = null,
        height: Int? = null,
        detectedPanels: List<Page.Panel>? = null
    ): Result<Unit> {
        return chapterRepository.updatePageDetails(
            pageId = pageId,
            filePath = filePath,
            width = width,
            height = height,
            detectedPanels = detectedPanels
        )
    }
}