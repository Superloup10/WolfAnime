package fr.wolfdev.wolfanime.domain

import kotlinx.serialization.Serializable

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
data class Media(
    val id: Int,
    val title: MediaTitle,
    val episodes: Int?,
    val format: MediaFormat,
    val crunchyroll: Crunchyroll?
)

data class MediaDTO(val media: Media)
