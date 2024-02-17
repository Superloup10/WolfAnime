package fr.wolfdev.wolfanime.domain.service

import fr.wolfdev.wolfanime.domain.Crunchyroll
import fr.wolfdev.wolfanime.domain.Media
import fr.wolfdev.wolfanime.domain.MediaFormat
import fr.wolfdev.wolfanime.domain.MediaTitle
import fr.wolfdev.wolfanime.domain.repository.MediaRepository
import fr.wolfdev.wolfanime.utils.AnilistMedia

class MediaService(private val mediaRepository: MediaRepository) {
    suspend fun getAllMedia(mediaIds: List<Int>): List<Media?> {
        return mediaRepository.getAllMedia(mediaIds)
    }

    suspend fun addBatchInsertMedia(medias: List<AnilistMedia?>, titles: List<MediaTitle?>, crunchyrolls: List<Crunchyroll?>): List<Media> {
        return mediaRepository.addBatchInsertMedia(medias, titles, crunchyrolls)
    }

    suspend fun updateFormatMedia(id: Int, format: MediaFormat): Int {
        return mediaRepository.updateFormatMedia(id, format)
    }
}
