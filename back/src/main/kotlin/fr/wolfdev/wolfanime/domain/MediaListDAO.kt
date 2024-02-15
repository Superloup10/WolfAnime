package fr.wolfdev.wolfanime.domain

import fr.wolfdev.wolfanime.domain.repository.Users
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date

/**
 * Media list watching status enum.
 */
enum class MediaListStatus {
    /**
     * Finished watching
     */
    COMPLETED,

    /**
     * Currently watching
     */
    CURRENT,

    /**
     * Stopped watching before completing
     */
    DROPPED,

    /**
     * Paused watching
     */
    PAUSED,

    /**
     * Planning to watch
     */
    PLANNING,

    /**
     * Re-watching
     */
    REPEATING
}

@Serializable
data class MediaList(
    val id: Int,
    val user: User,
    val media: Media,
    val status: MediaListStatus,
    val startedAt: LocalDate?,
    val completedAt: LocalDate?,
    val progress: Int,
    val repeat: Int
)

/*class MediaListDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MediaListDAO>(MediaListTable)

    var user by UserDAO referencedOn MediaListTable.user
    var media by MediaDAO referencedOn MediaListTable.media
    var status by MediaListTable.status
    var startedAt by MediaListTable.startedAt
    var completedAt by MediaListTable.completedAt
    var progress by MediaListTable.progress
    var repeat by MediaListTable.repeat
}*/

object MediaListTable : Table() {
    val id = integer("id")
    val user = integer("user_id") references Users.id
    val media = integer("media_id") references MediaTable.id
    val status = enumerationByName<MediaListStatus>("status", 255)
    val startedAt = date("started_at").nullable()
    val completedAt = date("completed_at").nullable()
    val progress = integer("progress")
    val repeat = integer("repeat")
    override val primaryKey = PrimaryKey(id)
}
