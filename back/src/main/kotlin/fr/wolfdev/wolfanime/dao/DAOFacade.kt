package fr.wolfdev.wolfanime.dao

import fr.wolfdev.wolfanime.utils.AnilistMedia
import fr.wolfdev.wolfanime.utils.AnilistMediaList
import fr.wolfdev.wolfanime.utils.AnilistMediaTitle
import fr.wolfdev.wolfanime.domain.*
import kotlinx.datetime.LocalDate

interface DAOFacade {
    suspend fun addMediaTitle(romaji: String, english: String, native: String): MediaTitle
    suspend fun addBatchInsertMediaTitle(mediaTitles: List<AnilistMediaTitle?>): List<MediaTitle>
    suspend fun getMediaTitle(romaji: String): MediaTitle?
    suspend fun getAllMediaTitle(romajiList: List<String>): List<MediaTitle?>
    suspend fun addCrunchyroll(originalUrl: String, correctedUrl: String): Crunchyroll
    suspend fun addBatchInsertCrunchyroll(urlMap: Map<String, String>): List<Crunchyroll>
    suspend fun getCrunchyroll(originalUrl: String): Crunchyroll?
    suspend fun getAllCrunchyroll(urlList: List<String>): List<Crunchyroll?>
    suspend fun addMedia(id: Int, title: MediaTitle, episodes: Int?, format: MediaFormat, crunchyroll: Crunchyroll?): Media
    suspend fun addBatchInsertMedia(medias: List<AnilistMedia?>, titles: List<MediaTitle?>, crunchyrolls: List<Crunchyroll?>): List<Media>
    suspend fun getMedia(id: Int): Media?
    suspend fun getAllMedia(mediaIds: List<Int>): List<Media?>
    suspend fun editMedia(id: Int, episodes: Int?): Int
    suspend fun updateFormatMedia(id: Int, format: MediaFormat): Int
    suspend fun addMediaList(id: Int, user: User, media: Media, status: MediaListStatus, datePair: Pair<LocalDate?, LocalDate?>, progress: Int, repeat: Int): MediaList
    suspend fun addBatchInsertMediaList(mediaLists: List<AnilistMediaList?>, user: User, medias: List<Media?>): List<MediaList>
    suspend fun getMediaList(user: User, media: Media): MediaList?
    suspend fun getAllMediaList(mediaListId: List<Int>): List<MediaList?>
    suspend fun updateProgressMedia(id: Int, progress: Int): Int
    suspend fun getNameMediaListCompleteWithStartedAtIsNull(name: String): List<String>
    suspend fun getMediaListByUsername(user: User): Map<Int, MediaList>
    suspend fun changingStatus(id: Int, status: MediaListStatus): Int
}
