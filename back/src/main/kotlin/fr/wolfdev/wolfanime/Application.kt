package fr.wolfdev.wolfanime

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import fr.wolfdev.wolfanime.config.DatabaseFactory
import fr.wolfdev.wolfanime.config.ModulesConfig
import fr.wolfdev.wolfanime.domain.Series
import fr.wolfdev.wolfanime.domain.service.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.kodein.di.instance
import org.slf4j.event.Level
import java.net.URI
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
        }
    }
    install(Resources)
    install(ContentNegotiation) {
        json()
    }
    install(ShutDownUrl.ApplicationCallPlugin) {
        // The URL that will be intercepted (you can also use the application.conf's ktor.deployment.shutdown.url key)
        shutDownUrl = "/shutdown"
        // A function that will be executed to get the exit code of the process
        exitCodeSupplier = { 0 } // ApplicationCall.() -> Int
    }
    DatabaseFactory.init(environment.config)
    val userController by ModulesConfig.kodein.instance<UserService>()
    val mediaTitleController by ModulesConfig.kodein.instance<MediaTitleService>()
    val crunchyrollController by ModulesConfig.kodein.instance<CrunchyrollService>()
    val mediaController by ModulesConfig.kodein.instance<MediaService>()
    val mediaListController by ModulesConfig.kodein.instance<MediaListService>()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/crunchyroll") {
            /* GYZJ43JMR <- Tensei Shitara Slime Datta Ken */
            val crunchyrollNative = CrunchyrollNative.getCrunchyrollDataBySeriesId("GYZJ43JMR")
            call.respond(mapOf("GYZJ43JMR" to Json.decodeFromString<Series>(crunchyrollNative))) // TODO : Replace by dynamique map
        }
        getAnilistByUsername(userController, mediaTitleController, crunchyrollController, mediaController, mediaListController)
        get("/animeName") {
            if (call.request.queryParameters.isEmpty()) {
                return@get call.respond(HttpStatusCode.BadRequest, "/animeName?username=<username>")
            }
            val username = call.request.queryParameters["username"]
            if (username.isNullOrEmpty()) {
                return@get call.respond(HttpStatusCode.BadRequest, "Vous n'avez pas saisi de nom d'utilisateur !")
            }
            call.respond(mediaListController.getNameMediaListCompleteWithStartedAtIsNull(username))
        }
    }
}

internal val httpClient = HttpClient(OkHttp) {
    engine {
        config {
            connectTimeout(10L, TimeUnit.SECONDS)
            readTimeout(60L, TimeUnit.SECONDS)
            connectTimeout(60L, TimeUnit.SECONDS)
        }
    }

    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.HEADERS
    }
}

val client = GraphQLKtorClient(URI("https://graphql.anilist.co").toURL(), httpClient)
