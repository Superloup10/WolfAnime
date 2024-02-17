package fr.wolfdev.wolfanime.domain.service

import fr.wolfdev.wolfanime.domain.Media
import fr.wolfdev.wolfanime.domain.MediaList
import fr.wolfdev.wolfanime.domain.User
import fr.wolfdev.wolfanime.domain.repository.MediaListRepository
import fr.wolfdev.wolfanime.utils.AnilistMediaList

class MediaListService(private val mediaListRepository: MediaListRepository) {
    suspend fun getAllMediaList(mediaListId: List<Int>): List<MediaList?> {
        return mediaListRepository.getAllMediaList(mediaListId)
    }

    suspend fun addBatchInsertMediaList(mediaLists: List<AnilistMediaList?>, user: User, medias: List<Media?>): List<MediaList> {
        return mediaListRepository.addBatchInsertMediaList(mediaLists, user, medias)
    }

    suspend fun getNameMediaListCompleteWithStartedAtIsNull(name: String): List<String> {
        return mediaListRepository.getNameMediaListCompleteWithStartedAtIsNull(name)
    }
}
