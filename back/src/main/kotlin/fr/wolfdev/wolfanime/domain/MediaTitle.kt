package fr.wolfdev.wolfanime.domain

import kotlinx.serialization.Serializable

@Serializable
data class MediaTitle(
    val id: Int,
    val romaji: String,
    val english: String? = null,
    val native: String
)

data class MediaTitleDTO(val mediaTitle: MediaTitle?)
