package fr.wolfdev.wolfanime

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World!", response.bodyAsText())
    }

    /*@Test
    fun testCrunchyroll() = testApplication {
        val response = client.get("/crunchyroll")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Crunchyroll!", response.bodyAsText())
    }*/

    @Test
    fun testAnilist() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.get("/anilist") {
            url {
                parameters.append("username", "Superloup10")
            }
        }
        when {
            response.request.url.parameters.isEmpty() -> {
                assertEquals(HttpStatusCode.BadRequest, response.status)
                assertEquals("/anilist?username=<username>", response.bodyAsText())
            }

            response.request.url.parameters["username"].isNullOrEmpty() -> {
                assertEquals(HttpStatusCode.BadRequest, response.status)
                assertEquals("Vous n'avez pas saisi de nom d'utilisateur !", response.bodyAsText())
            }

            else -> {
                assertEquals(HttpStatusCode.OK, response.status)
            }
        }
        /*if (response.status == HttpStatusCode.NotFound) {
            assertEquals("Utilisateur non trouvé !", response.bodyAsText())
        }*/
    }

    @Test
    fun testAnimeName() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.get("/animeName") {
            url {
                parameters.append("username", "Superloup10")
            }
        }
        when {
            response.request.url.parameters.isEmpty() -> {
                assertEquals(HttpStatusCode.BadRequest, response.status)
                assertEquals("/animeName?username=<username>", response.bodyAsText())
            }

            response.request.url.parameters["username"].isNullOrEmpty() -> {
                assertEquals(HttpStatusCode.BadRequest, response.status)
                assertEquals("Vous n'avez pas saisi de nom d'utilisateur !", response.bodyAsText())
            }

            else -> {
                assertEquals(HttpStatusCode.OK, response.status)
                assertEquals(emptyList<String>(), response.body())
            }
        }
    }
}
