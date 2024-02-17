package fr.wolfdev.wolfanime.domain.repository

import fr.wolfdev.wolfanime.config.DatabaseFactory.dbQuery
import fr.wolfdev.wolfanime.domain.Media
import fr.wolfdev.wolfanime.domain.MediaList
import fr.wolfdev.wolfanime.domain.MediaListStatus
import fr.wolfdev.wolfanime.domain.User
import fr.wolfdev.wolfanime.utils.AnilistMediaList
import fr.wolfdev.wolfanime.utils.datePair
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.transactions.transaction

internal object MediaListTable : Table() {
    val id = integer("id")
    val user = integer("user_id") references Users.id
    val media = integer("media_id") references MediaTable.id
    val status = enumerationByName<MediaListStatus>("status", 255)
    val startedAt = date("started_at").nullable()
    val completedAt = date("completed_at").nullable()
    val progress = integer("progress")
    val repeat = integer("repeat")
    override val primaryKey = PrimaryKey(id)

    fun toDomain(row: ResultRow, user: User = Users.toDomain(row), media: Media = MediaTable.toDomain(row)) = MediaList(
        row[id],
        user,
        media,
        row[status],
        row[startedAt],
        row[completedAt],
        row[progress],
        row[repeat]
    )
}

class MediaListRepository {
    init {
        transaction {
            SchemaUtils.create(MediaListTable)
        }
    }

    suspend fun addMediaList(id: Int, user: User, media: Media, status: MediaListStatus, datePair: Pair<LocalDate?, LocalDate?>, progress: Int, repeat: Int): MediaList = dbQuery {
        MediaListTable.insert {
            it[this.id] = id
            it[this.user] = user.id
            it[this.media] = media.id
            it[this.status] = status
            it[this.startedAt] = datePair.first
            it[this.completedAt] = datePair.second
            it[this.progress] = progress
            it[this.repeat] = repeat
        }.resultedValues?.map { MediaListTable.toDomain(it, user, media) }?.first()!!
    }

    suspend fun addBatchInsertMediaList(mediaLists: List<AnilistMediaList?>, user: User, medias: List<Media?>): List<MediaList> = dbQuery {
        MediaListTable.batchInsert(mediaLists, shouldReturnGeneratedValues = false) { mediaList ->
            this[MediaListTable.id] = mediaList?.id!!
            this[MediaListTable.user] = user.id
            this[MediaListTable.media] = medias.first { it?.id == mediaList.media?.id }?.id!!
            this[MediaListTable.status] = MediaListStatus.valueOf(mediaList.status?.name!!)
            this[MediaListTable.startedAt] = mediaList.datePair().first
            this[MediaListTable.completedAt] = mediaList.datePair().second
            this[MediaListTable.progress] = mediaList.progress!!
            this[MediaListTable.repeat] = mediaList.repeat!!
        }.map { MediaListTable.toDomain(it, user, medias.first { media -> media?.id == it[MediaListTable.media] }!!) }
    }

    suspend fun getMediaList(user: User, media: Media): MediaList? = dbQuery {
        MediaListTable.selectAll()
            .where { (MediaListTable.user eq user.id) and (MediaListTable.media eq media.id) }
            .map { MediaListTable.toDomain(it, user, media) }
            .firstOrNull()
    }

    suspend fun getAllMediaList(mediaListId: List<Int>): List<MediaList?> = dbQuery {
        (MediaListTable innerJoin Users innerJoin MediaTable innerJoin MediaTitleTable leftJoin CrunchyrollTable)
            .selectAll()
            .where { MediaListTable.id inList mediaListId }
            .map {
                MediaListTable.toDomain(
                    it,
                    media = MediaTable.toDomain(
                        it,
                        crunchyroll = (if (it[MediaTable.crunchyroll] != null) CrunchyrollTable.toDomain(it) else null)
                    )
                )
            }
    }

    suspend fun updateProgressMedia(id: Int, progress: Int): Int = dbQuery {
        MediaListTable.update({
            (MediaListTable.id eq id) and (MediaListTable.progress neq progress)
        }) {
            it[this.progress] = progress
        }
    }

    suspend fun getNameMediaListCompleteWithStartedAtIsNull(name: String): List<String> = dbQuery {
        (MediaTitleTable innerJoin MediaTable innerJoin MediaListTable innerJoin Users)
            .select(MediaTitleTable.id, MediaTitleTable.romaji)
            .where {
                (MediaListTable.status eq MediaListStatus.COMPLETED) and
                        MediaListTable.startedAt.isNull() and
                        (Users.name eq name)
            }
            .orderBy(MediaTitleTable.romaji).map { it[MediaTitleTable.romaji] }
    }

    suspend fun getMediaListByUsername(user: User): Map<Int, MediaList> = dbQuery {
        (MediaListTable innerJoin Users innerJoin MediaTable innerJoin MediaTitleTable)
            .selectAll().where { MediaListTable.user eq user.id }
            .map { MediaListTable.toDomain(it) }.associateBy { it.id }
    }

    suspend fun changingStatus(id: Int, status: MediaListStatus): Int = dbQuery {
        MediaListTable.update({
            (MediaListTable.id eq id) and (MediaListTable.status neq status)
        }) {
            it[this.status] = status
        }
    }
}
