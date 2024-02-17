package fr.wolfdev.wolfanime.domain.repository

import fr.wolfdev.wolfanime.config.DatabaseFactory.dbQuery
import fr.wolfdev.wolfanime.domain.Crunchyroll
import fr.wolfdev.wolfanime.domain.Media
import fr.wolfdev.wolfanime.domain.MediaFormat
import fr.wolfdev.wolfanime.domain.MediaTitle
import fr.wolfdev.wolfanime.utils.AnilistMedia
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

internal object MediaTable : Table() {
    val id = integer("id")
    val title = reference("title_id", MediaTitleTable)
    val episodes = integer("episodes").nullable()
    val format = enumerationByName<MediaFormat>("format", 255)
    val crunchyroll = optReference("crunchyroll_id", CrunchyrollTable.id)
    override val primaryKey = PrimaryKey(id)

    fun toDomain(
        row: ResultRow,
        title: MediaTitle = MediaTitleTable.toDomain(row),
        crunchyroll: Crunchyroll? = CrunchyrollTable.toDomain(row)
    ) = Media(
        row[id],
        title,
        row[episodes],
        row[format],
        crunchyroll
    )
}

class MediaRepository {
    init {
        transaction {
            SchemaUtils.create(MediaTable)
        }
    }

    suspend fun addMedia(id: Int, title: MediaTitle, episodes: Int?, format: MediaFormat, crunchyroll: Crunchyroll?): Media = dbQuery {
        MediaTable.insert {
            it[this.id] = id
            it[this.title] = title.id
            it[this.episodes] = episodes
            it[this.format] = format
            it[this.crunchyroll] = crunchyroll?.id
        }.resultedValues?.map { MediaTable.toDomain(it, title, crunchyroll) }?.first()!!
    }

    suspend fun addBatchInsertMedia(medias: List<AnilistMedia?>, titles: List<MediaTitle?>, crunchyrolls: List<Crunchyroll?>): List<Media> = dbQuery {
        MediaTable.batchInsert(medias, shouldReturnGeneratedValues = false) { anilistMedia ->
            val crunchyrollIfPresent = anilistMedia?.externalLinks?.firstOrNull { externalLink ->
                externalLink?.url?.contains("crunchyroll") == true
            }?.url
            this[MediaTable.id] = anilistMedia?.id!!
            this[MediaTable.title] = titles.first { it?.romaji == anilistMedia.title?.romaji }?.id!!
            this[MediaTable.episodes] = anilistMedia.episodes
            this[MediaTable.format] = MediaFormat.valueOf(anilistMedia.format?.name ?: "EMPTY")
            this[MediaTable.crunchyroll] = crunchyrolls.firstOrNull { it?.originalUrl == crunchyrollIfPresent }?.id
        }.map {
            MediaTable.toDomain(
                it,
                titles.first { title -> title?.id == it[MediaTable.title].value }!!,
                crunchyrolls.firstOrNull { crunchyroll -> crunchyroll?.id == it[MediaTable.crunchyroll]?.value }
            )
        }
    }

    suspend fun getMedia(id: Int): Media? = dbQuery {
        (MediaTable innerJoin MediaTitleTable)
            .selectAll()
            .where { MediaTable.id eq id }
            .map {
                MediaTable.toDomain(
                    row = it,
                    crunchyroll = (if (it[MediaTable.crunchyroll] != null) CrunchyrollTable.toDomain(it) else null)
                )
            }
            .firstOrNull()
    }

    suspend fun getAllMedia(mediaIds: List<Int>): List<Media?> = dbQuery {
        (MediaTable innerJoin MediaTitleTable leftJoin CrunchyrollTable)
            .selectAll()
            .where { MediaTable.id inList mediaIds }
            .map {
                MediaTable.toDomain(
                    row = it,
                    crunchyroll = (if (it[MediaTable.crunchyroll] != null) CrunchyrollTable.toDomain(it) else null)
                )
            }
    }

    suspend fun editMedia(id: Int, episodes: Int?): Int = dbQuery {
        MediaTable.update({
            (MediaTable.id eq id) and (MediaTable.episodes neq episodes)
        }) {
            it[this.episodes] = episodes
        }
    }

    suspend fun updateFormatMedia(id: Int, format: MediaFormat): Int = dbQuery {
        MediaTable.update({
            (MediaTable.id eq id) and (MediaTable.format eq MediaFormat.EMPTY)
        }) { it[this.format] = format }
    }
}
