package fr.wolfdev.wolfanime.domain

import kotlinx.serialization.Serializable

@Serializable
data class Crunchyroll(
    val id: Int,
    val originalUrl: String,
    val correctedUrl: String,
    val series: String
)

data class CrunchyrollDTO(val crunchyroll: Crunchyroll)
