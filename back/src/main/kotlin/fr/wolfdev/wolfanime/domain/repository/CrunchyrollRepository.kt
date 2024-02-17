package fr.wolfdev.wolfanime.domain.repository

import fr.wolfdev.wolfanime.config.DatabaseFactory.dbQuery
import fr.wolfdev.wolfanime.domain.Crunchyroll
import fr.wolfdev.wolfanime.utils.CRUNCHYROLL_URL_REGEX
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

internal object CrunchyrollTable : IntIdTable() {
    val originalUrl = varchar("original_url", 255)
    val correctedUrl = varchar("corrected_url", 255)
    val seriesId = varchar("series_id", 255)

    fun toDomain(row: ResultRow) = Crunchyroll(row[id].value, row[originalUrl], row[correctedUrl], row[seriesId])
}

class CrunchyrollRepository {
    init {
        transaction {
            SchemaUtils.create(CrunchyrollTable)
        }
    }

    suspend fun addCrunchyroll(originalUrl: String, correctedUrl: String): Crunchyroll = dbQuery {
        val seriesId = CRUNCHYROLL_URL_REGEX.find(correctedUrl)?.groups?.get("id")?.value ?: "erreur d'id"
        CrunchyrollTable.insert {
            it[this.originalUrl] = originalUrl
            it[this.correctedUrl] = correctedUrl
            it[this.seriesId] = seriesId
        }.resultedValues?.map { CrunchyrollTable.toDomain(it) }?.first()!!
    }

    suspend fun addBatchInsertCrunchyroll(urlMap: Map<String, String>): List<Crunchyroll> = dbQuery {
        CrunchyrollTable.batchInsert(urlMap.keys.toList()) {
            val seriesId = CRUNCHYROLL_URL_REGEX.find(urlMap.getValue(it))?.groups?.get("id")?.value ?: "erreur d'id"
            this[CrunchyrollTable.originalUrl] = it
            this[CrunchyrollTable.correctedUrl] = urlMap.getValue(it)
            this[CrunchyrollTable.seriesId] = seriesId
        }.map { CrunchyrollTable.toDomain(it) }
    }

    suspend fun getCrunchyroll(originalUrl: String): Crunchyroll? = dbQuery {
        CrunchyrollTable.selectAll()
            .where { CrunchyrollTable.originalUrl eq originalUrl }
            .map { CrunchyrollTable.toDomain(it) }.firstOrNull()
    }

    suspend fun getAllCrunchyroll(urlList: List<String>): List<Crunchyroll?> = dbQuery {
        CrunchyrollTable.selectAll()
            .where { CrunchyrollTable.originalUrl inList urlList }
            .map { CrunchyrollTable.toDomain(it) }
    }
}
