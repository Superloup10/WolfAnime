package fr.wolfdev.wolfanime.domain

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

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

data class MediaListDTO(val mediaList: MediaList)
