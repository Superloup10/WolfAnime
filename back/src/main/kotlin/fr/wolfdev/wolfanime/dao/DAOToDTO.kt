package fr.wolfdev.wolfanime.dao

import fr.wolfdev.wolfanime.domain.*
import fr.wolfdev.wolfanime.domain.repository.Users
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toCrunchyroll() = Crunchyroll(
    this[CrunchyrollTable.id].value,
    this[CrunchyrollTable.originalUrl],
    this[CrunchyrollTable.correctedUrl],
    this[CrunchyrollTable.seriesId],
)

fun ResultRow.toMediaTitle() = MediaTitle(
    this[MediaTitleTable.id].value,
    this[MediaTitleTable.romaji],
    this[MediaTitleTable.english],
    this[MediaTitleTable.native]
)

fun ResultRow.toMedia(title: MediaTitle = toMediaTitle(), crunchyroll: Crunchyroll? = toCrunchyroll()) = Media(
    this[MediaTable.id],
    title,
    this[MediaTable.episodes],
    this[MediaTable.format],
    crunchyroll
)

fun ResultRow.toMediaList(user: User = Users.toDomain(this), media: Media = toMedia()) = MediaList(
    this[MediaListTable.id],
    user,
    media,
    this[MediaListTable.status],
    this[MediaListTable.startedAt],
    this[MediaListTable.completedAt],
    this[MediaListTable.progress],
    this[MediaListTable.repeat]
)

/*fun UserDAO.toDTO() = User(id.value, name)
fun MediaTitleDAO.toDTO() = MediaTitle(id.value, romaji, english, native)
fun MediaDAO.toDTO() = Media(id.value, title.toDTO(), episodes)
fun MediaListDAO.toDTO() = MediaList(
    id.value,
    user.toDTO(),
    media.toDTO(),
    status,
    startedAt,
    completedAt,
    progress,
    repeat
)*/
