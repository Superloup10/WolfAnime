package fr.wolfdev.wolfanime.dao

import fr.wolfdev.wolfanime.dao.DatabaseFactory.dbQuery
import fr.wolfdev.wolfanime.domain.*
import fr.wolfdev.wolfanime.domain.repository.Users
import fr.wolfdev.wolfanime.utils.*
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.*

class DAOFacadeImpl : DAOFacade {
    override suspend fun addMediaTitle(romaji: String, english: String, native: String): MediaTitle = dbQuery {
        MediaTitleTable.insert {
            it[this.romaji] = romaji
            it[this.english] = english
            it[this.native] = native
        }.resultedValues?.first()!!.toMediaTitle()
    }

    override suspend fun addBatchInsertMediaTitle(mediaTitles: List<AnilistMediaTitle?>): List<MediaTitle> = dbQuery {
        MediaTitleTable.batchInsert(mediaTitles) {
            this[MediaTitleTable.romaji] = it?.romaji ?: "romaji"
            this[MediaTitleTable.english] = it?.english
            this[MediaTitleTable.native] = it?.native ?: "native"
        }.map { it.toMediaTitle() }
    }

    override suspend fun getMediaTitle(romaji: String): MediaTitle? = dbQuery {
        MediaTitleTable.selectAll().where { MediaTitleTable.romaji eq romaji }.map { it.toMediaTitle() }.firstOrNull()
    }

    override suspend fun getAllMediaTitle(romajiList: List<String>): List<MediaTitle?> = dbQuery {
        MediaTitleTable.selectAll()
            .where { MediaTitleTable.romaji inList romajiList }
            .map { it.toMediaTitle() }
    }

    override suspend fun addCrunchyroll(originalUrl: String, correctedUrl: String): Crunchyroll = dbQuery {
        val seriesId = CRUNCHYROLL_URL_REGEX.find(correctedUrl)?.groups?.get("id")?.value ?: "erreur d'id"
        CrunchyrollTable.insert {
            it[this.originalUrl] = originalUrl
            it[this.correctedUrl] = correctedUrl
            it[this.seriesId] = seriesId
        }.resultedValues?.first()!!.toCrunchyroll()
    }

    override suspend fun addBatchInsertCrunchyroll(urlMap: Map<String, String>): List<Crunchyroll> = dbQuery {
        CrunchyrollTable.batchInsert(urlMap.keys.toList()) {
            val seriesId = CRUNCHYROLL_URL_REGEX.find(urlMap.getValue(it))?.groups?.get("id")?.value ?: "erreur d'id"
            this[CrunchyrollTable.originalUrl] = it
            this[CrunchyrollTable.correctedUrl] = urlMap.getValue(it)
            this[CrunchyrollTable.seriesId] = seriesId
        }.map { it.toCrunchyroll() }
    }

    override suspend fun getCrunchyroll(originalUrl: String): Crunchyroll? = dbQuery {
        CrunchyrollTable.selectAll().where { CrunchyrollTable.originalUrl eq originalUrl }.map { it.toCrunchyroll() }.firstOrNull()
    }

    override suspend fun getAllCrunchyroll(urlList: List<String>): List<Crunchyroll?> = dbQuery {
        CrunchyrollTable.selectAll().where { CrunchyrollTable.originalUrl inList urlList }.map { it.toCrunchyroll() }
    }

    override suspend fun addMedia(id: Int, title: MediaTitle, episodes: Int?, format: MediaFormat, crunchyroll: Crunchyroll?): Media = dbQuery {
        MediaTable.insert {
            it[this.id] = id
            it[this.title] = title.id
            it[this.episodes] = episodes
            it[this.format] = format
            it[this.crunchyroll] = crunchyroll?.id
        }.resultedValues?.first()!!.toMedia(title, crunchyroll)
    }

    override suspend fun addBatchInsertMedia(medias: List<AnilistMedia?>, titles: List<MediaTitle?>, crunchyrolls: List<Crunchyroll?>): List<Media> = dbQuery {
        MediaTable.batchInsert(medias, shouldReturnGeneratedValues = false) { anilistMedia ->
            val crunchyrollIfPresent = anilistMedia?.externalLinks?.firstOrNull { externalLink ->
                externalLink?.url?.contains("crunchyroll") == true
            }?.url
            this[MediaTable.id] = anilistMedia?.id!!
            this[MediaTable.title] = titles.first { it?.romaji == anilistMedia.title?.romaji }?.id!!
            this[MediaTable.episodes] = anilistMedia.episodes
            this[MediaTable.format] = MediaFormat.valueOf(anilistMedia.format?.name!!)
            this[MediaTable.crunchyroll] = crunchyrolls.firstOrNull { it?.originalUrl == crunchyrollIfPresent }?.id
        }.map {
            it.toMedia(
                titles.first { title -> title?.id == it[MediaTable.title].value }!!,
                crunchyrolls.firstOrNull { crunchyroll -> crunchyroll?.id == it[MediaTable.crunchyroll]?.value }
            )
        }
    }

    override suspend fun getMedia(id: Int): Media? = dbQuery {
        (MediaTable innerJoin MediaTitleTable)
            .selectAll()
            .where { MediaTable.id eq id }
            .map { it.toMedia(crunchyroll = (if (it[MediaTable.crunchyroll] != null) it.toCrunchyroll() else null)) }
            .firstOrNull()
    }

    override suspend fun getAllMedia(mediaIds: List<Int>): List<Media?> = dbQuery {
        (MediaTable innerJoin MediaTitleTable leftJoin CrunchyrollTable)
            .selectAll()
            .where { MediaTable.id inList mediaIds }
            .map { it.toMedia(crunchyroll = (if (it[MediaTable.crunchyroll] != null) it.toCrunchyroll() else null)) }
    }

    override suspend fun editMedia(id: Int, episodes: Int?): Int = dbQuery {
        MediaTable.update({
            (MediaTable.id eq id) and (MediaTable.episodes neq episodes)
        }) {
            it[this.episodes] = episodes
        }
    }

    override suspend fun updateFormatMedia(id: Int, format: MediaFormat): Int = dbQuery {
        MediaTable.update({
            (MediaTable.id eq id) and (MediaTable.format eq MediaFormat.EMPTY)
        }) { it[this.format] = format }
    }

    override suspend fun addMediaList(id: Int, user: User, media: Media, status: MediaListStatus, datePair: Pair<LocalDate?, LocalDate?>, progress: Int, repeat: Int): MediaList = dbQuery {
        MediaListTable.insert {
            it[this.id] = id
            it[this.user] = user.id
            it[this.media] = media.id
            it[this.status] = status
            it[this.startedAt] = datePair.first
            it[this.completedAt] = datePair.second
            it[this.progress] = progress
            it[this.repeat] = repeat
        }.resultedValues?.first()!!.toMediaList(user, media)
    }

    override suspend fun addBatchInsertMediaList(mediaLists: List<AnilistMediaList?>, user: User, medias: List<Media?>): List<MediaList> = dbQuery {
        MediaListTable.batchInsert(mediaLists, shouldReturnGeneratedValues = false) { mediaList ->
            this[MediaListTable.id] = mediaList?.id!!
            this[MediaListTable.user] = user.id
            this[MediaListTable.media] = medias.first { it?.id == mediaList.media?.id }?.id!!
            this[MediaListTable.status] = MediaListStatus.valueOf(mediaList.status?.name!!)
            this[MediaListTable.startedAt] = mediaList.datePair().first
            this[MediaListTable.completedAt] = mediaList.datePair().second
            this[MediaListTable.progress] = mediaList.progress!!
            this[MediaListTable.repeat] = mediaList.repeat!!
        }.map { it.toMediaList(user, medias.first { media -> media?.id == it[MediaListTable.media] }!!) }
    }

    override suspend fun getMediaList(user: User, media: Media): MediaList? = dbQuery {
        MediaListTable.selectAll()
            .where { (MediaListTable.user eq user.id) and (MediaListTable.media eq media.id) }
            .map { it.toMediaList(user, media) }
            .firstOrNull()
    }

    override suspend fun getAllMediaList(mediaListId: List<Int>): List<MediaList?> = dbQuery {
        (MediaListTable innerJoin Users innerJoin MediaTable innerJoin MediaTitleTable leftJoin CrunchyrollTable)
            .selectAll()
            .where { MediaListTable.id inList mediaListId }
            .map {
                it.toMediaList(
                    media = it.toMedia(
                        crunchyroll = (if (it[MediaTable.crunchyroll] != null) it.toCrunchyroll() else null)
                    )
                )
            }
    }

    override suspend fun updateProgressMedia(id: Int, progress: Int): Int = dbQuery {
        MediaListTable.update({
            (MediaListTable.id eq id) and (MediaListTable.progress neq progress)
        }) {
            it[this.progress] = progress
        }
    }

    override suspend fun getNameMediaListCompleteWithStartedAtIsNull(name: String): List<String> = dbQuery {
        (MediaTitleTable innerJoin MediaTable innerJoin MediaListTable innerJoin Users)
            .select(MediaTitleTable.id, MediaTitleTable.romaji)
            .where {
                (MediaListTable.status eq MediaListStatus.COMPLETED) and
                        MediaListTable.startedAt.isNull() and
                        (Users.name eq name)
            }
            .orderBy(MediaTitleTable.romaji).map { it[MediaTitleTable.romaji] }
    }

    override suspend fun getMediaListByUsername(user: User): Map<Int, MediaList> = dbQuery {
        (MediaListTable innerJoin Users innerJoin MediaTable innerJoin MediaTitleTable)
            .selectAll().where { MediaListTable.user eq user.id }
            .map { it.toMediaList() }.associateBy { it.id }
    }

    override suspend fun changingStatus(id: Int, status: MediaListStatus): Int = dbQuery {
        MediaListTable.update({
            (MediaListTable.id eq id) and (MediaListTable.status neq status)
        }) {
            it[this.status] = status
        }
    }
}
