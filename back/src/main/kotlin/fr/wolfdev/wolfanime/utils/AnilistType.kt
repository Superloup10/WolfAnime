package fr.wolfdev.wolfanime.utils

import fr.wolfdev.wolfanime.graphql.generated.animelistgetbyuserid.Media
import fr.wolfdev.wolfanime.graphql.generated.animelistgetbyuserid.MediaList
import fr.wolfdev.wolfanime.graphql.generated.animelistgetbyuserid.MediaTitle
import fr.wolfdev.wolfanime.graphql.generated.useridgetbyname.User
import kotlinx.datetime.LocalDate

typealias AnilistMediaTitle = MediaTitle
typealias AnilistMedia = Media
typealias AnilistMediaList = MediaList
typealias AnilistUser = User

internal fun AnilistMediaList.datePair(): Pair<LocalDate?, LocalDate?> {
    val startedAt: LocalDate?
    val completedAt: LocalDate?
    val yearStarted = this.startedAt?.year
    val monthStarted = this.startedAt?.month
    val dayStarted = this.startedAt?.day
    val yearCompleted = this.completedAt?.year
    val monthCompleted = this.completedAt?.month
    val dayCompleted = this.completedAt?.day

    when (status?.name) {
        "PLANNING" -> {
            startedAt = null
            completedAt = null
        }

        "CURRENT", "DROPPED", "PAUSED" -> {
            startedAt = LocalDate(yearStarted ?: 1, monthStarted ?: 1, dayStarted ?: 1)
            completedAt = null
        }

        "COMPLETED" -> {
            completedAt = LocalDate(yearCompleted!!, monthCompleted!!, dayCompleted!!)
            startedAt = if (yearStarted == null && monthStarted == null && dayStarted == null) {
                null
            } else {
                LocalDate(yearStarted!!, monthStarted!!, dayStarted!!)
            }
        }

        else -> {
            startedAt = LocalDate(yearStarted!!, monthStarted!!, dayStarted!!)
            completedAt = LocalDate(yearCompleted!!, monthCompleted!!, dayCompleted!!)
        }
    }
    return Pair(startedAt, completedAt)
}
