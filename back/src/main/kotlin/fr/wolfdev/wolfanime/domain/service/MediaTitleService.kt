package fr.wolfdev.wolfanime.domain.service

import fr.wolfdev.wolfanime.domain.MediaTitle
import fr.wolfdev.wolfanime.domain.repository.MediaTitleRepository
import fr.wolfdev.wolfanime.utils.AnilistMediaTitle

class MediaTitleService(private val mediaTitleRepository: MediaTitleRepository) {
    suspend fun getAllMediaTitle(romajiList: List<String>): List<MediaTitle?> {
        return mediaTitleRepository.getAllMediaTitle(romajiList)
    }

    suspend fun addBatchInsertMediaTitle(mediaTitles: List<AnilistMediaTitle?>): List<MediaTitle> {
        return mediaTitleRepository.addBatchInsertMediaTitle(mediaTitles)
    }
}
