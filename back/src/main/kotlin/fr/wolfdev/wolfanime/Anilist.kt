package fr.wolfdev.wolfanime

import fr.wolfdev.wolfanime.domain.service.*
import fr.wolfdev.wolfanime.graphql.generated.AnimeListGetByUserId
import fr.wolfdev.wolfanime.graphql.generated.UserIdGetByName
import io.ktor.client.request.get
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

@Resource("/anilist")
class Anilist(val username: String? = null) {
    @Resource("format")
    class Format
}

fun Route.getAnilistByUsername(
    userService: UserService,
    mediaTitleService: MediaTitleService,
    crunchyrollService: CrunchyrollService,
    mediaService: MediaService,
    mediaListService: MediaListService
) {
    get<Anilist> { anilist ->
        if (call.request.queryParameters.isEmpty()) {
            return@get call.respond(HttpStatusCode.BadRequest, "/anilist?username=<username>")
        }
        // val animeGetById = AnimeGetById(AnimeGetById.Variables(101280)) // Tensei Shitara Slime Datta Ken
        val username = anilist.username
        if (username.isNullOrEmpty()) {
            return@get call.respond(HttpStatusCode.BadRequest, "Vous n'avez pas saisi de nom d'utilisateur !")
        }
        val userIdGetByName = UserIdGetByName(UserIdGetByName.Variables(username))
        val userQuery = client.execute(userIdGetByName)
        if (userQuery.data == null) {
            return@get call.respond(HttpStatusCode.NotFound, "Utilisateur non trouvé !")
        }
        val user = userService.getOrCreate(userQuery.data?.User?.id!!, username)
        val animeListGetByUserId = AnimeListGetByUserId(AnimeListGetByUserId.Variables(userQuery.data?.User?.id))
        val animeListQuery = client.execute(animeListGetByUserId)
        val mediaLists = animeListQuery.data?.MediaListCollection?.lists?.mapNotNull { group ->
            group?.entries?.map { it }
        }?.flatten()
        val medias = mediaLists?.map { it?.media }
        val titleList = medias?.map { it?.title }
        val urlList = medias?.mapNotNull {
            it?.externalLinks?.firstOrNull { externalLink ->
                externalLink?.url?.contains("crunchyroll") == true
            }?.url
        }

        /*animeListQuery.data?.MediaListCollection?.lists?.forEach { mediaListGroup: MediaListGroup? ->
             mediaListGroup?.entries?.forEach {
                 it?.apply {
                     val romaji = media?.title?.romaji ?: "romaji"
                     val english = media?.title?.english ?: "english"
                     val native = media?.title?.native ?: "native"

                     val mediaTitle = dao.mediaTitle(romaji) ?: dao.addNewMediaTitle(romaji, english, native)
                     val media = dao.media(media?.id!!) ?: dao.addNewMedia(media.id, mediaTitle, media.episodes)
                     val status = MediaListStatus.valueOf(status?.name!!)
                     val mediaList = dao.mediaList(user, media) ?: dao.addNewMediaList(id, user, media, status, datePair(), progress!!, repeat!!)
                     mediaListMap[mediaList.id] = mediaList
                 }
             }
         }*/
        // ----------------------------------------MEDIATITLE------------------------------------------------------//
        val existingTitles = mediaTitleService.getAllMediaTitle(titleList?.map { it?.romaji ?: "romaji" }!!)
        val newTitles = titleList.filter { title -> existingTitles.none { it?.romaji == title?.romaji } }
        val updatedTitles = handleNewData(existingTitles, newTitles) { mediaTitleService.addBatchInsertMediaTitle(it) }
        // ----------------------------------------CRUNCHYROLL-----------------------------------------------------//
        val existingUrls = crunchyrollService.getAllCrunchyroll(urlList!!)
        val newUrls = urlList.filter { url -> existingUrls.none { it?.originalUrl == url } }
            .associateWith { httpClient.get(it).request.url.toString() }
        val updatedUrls = handleNewData(existingUrls, newUrls) { crunchyrollService.addBatchInsertCrunchyroll(it) }
        // ----------------------------------------MEDIA-----------------------------------------------------------//
        /*medias.forEach {
            val dataUpdate = mediaService.updateFormatMedia(it?.id!!, MediaFormat.valueOf(it.format?.name ?: "EMPTY"))
            call.application.environment.log.debug("Nombre de ligne update : $dataUpdate")
        }*/
        val existingMedias = mediaService.getAllMedia(medias.map { it?.id!! })
        val newMedias = medias.filter { media -> existingMedias.none { it?.id == media?.id } }
        val updatedMedias = handleNewData(existingMedias, newMedias) {
            mediaService.addBatchInsertMedia(it, updatedTitles, updatedUrls)
        }
        // ----------------------------------------MEDIALIST-------------------------------------------------------//
        val existingMediaLists = mediaListService.getAllMediaList(mediaLists.map { it?.id!! })
        val newMediaLists = mediaLists.filter { mediaList -> existingMediaLists.none { it?.id == mediaList?.id } }
        val updatedMediaLists = handleNewData(existingMediaLists, newMediaLists) {
            mediaListService.addBatchInsertMediaList(it, user, updatedMedias)
        }
        call.respond(updatedMediaLists.associateBy { it?.id })
    }
}

private inline fun <OLD, NEW> handleNewData(existingData: List<OLD>, newData: Map<NEW, NEW>, addFunction: (Map<NEW, NEW>) -> List<OLD>): List<OLD> {
    return if (newData.isNotEmpty()) addFunction(newData) else existingData
}

private inline fun <OLD, NEW> handleNewData(existingData: List<OLD>, newData: List<NEW>, addFunction: (List<NEW>) -> List<OLD>): List<OLD> {
    return if (newData.isNotEmpty()) addFunction(newData) else existingData
}
