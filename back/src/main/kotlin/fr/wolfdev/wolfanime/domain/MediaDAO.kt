package fr.wolfdev.wolfanime.domain

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

/**
 * The format the media was released in
 */
enum class MediaFormat {
    EMPTY,

    /**
     * Anime broadcast on television
     */
    TV,

    /**
     * Anime which are under 15 minutes in length and broadcast on television
     */
    TV_SHORT,

    /**
     * Anime movies with a theatrical release
     */
    MOVIE,

    /**
     * Special episodes that have been included in DVD/Blu-ray releases, picture dramas, pilots, etc.
     */
    SPECIAL,

    /**
     * (Original Video Animation) Anime that have been released directly on DVD/Blu-ray without originally
     * going through a theatrical release or television broadcast
     */
    OVA,

    /**
     * (Original Net Animation) Anime that have been originally released online
     * or are only available through streaming services.
     */
    ONA,

    /**
     * Short Anime released as a music video
     */
    MUSIC,

    /**
     * Professionally published manga with more than one chapter
     */
    MANGA,

    /**
     * Written books released as a series of light novels
     */
    NOVEL,

    /**
     * Manga with just one chapter
     */
    ONE_SHOT
}

@Serializable
data class Crunchyroll(
    val id: Int,
    val originalUrl: String,
    val correctedUrl: String,
    val series: String
)

@Serializable
data class MediaTitle(
    val id: Int,
    val romaji: String,
    val english: String? = null,
    val native: String
)

/*class MediaTitleDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MediaTitleDAO>(MediaTitleTable)

    var romaji by MediaTitleTable.romaji
    var english by MediaTitleTable.english
    var native by MediaTitleTable.native
}*/

@Serializable
data class Media(
    val id: Int,
    val title: MediaTitle,
    val episodes: Int?,
    val format: MediaFormat,
    val crunchyroll: Crunchyroll?
)

/*class MediaDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MediaDAO>(MediaTable)

    var title by MediaTitleDAO referencedOn MediaTable.title
    var episodes by MediaTable.episodes
}*/

object CrunchyrollTable : IntIdTable() {
    val originalUrl = varchar("original_url", 255)
    val correctedUrl = varchar("corrected_url", 255)
    val seriesId = varchar("series_id", 255)
}

object MediaTitleTable : IntIdTable() {
    val romaji = varchar("romaji", 255)
    val english = varchar("english", 255).nullable()
    val native = varchar("native", 255)
}

object MediaTable : Table() {
    val id = integer("id")
    val title = reference("title_id", MediaTitleTable)
    val episodes = integer("episodes").nullable()
    val format = enumerationByName<MediaFormat>("format", 255)
    val crunchyroll = optReference("crunchyroll_id", CrunchyrollTable.id)
    override val primaryKey = PrimaryKey(id)
}
