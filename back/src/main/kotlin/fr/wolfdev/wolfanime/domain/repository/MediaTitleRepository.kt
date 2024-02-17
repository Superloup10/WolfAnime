package fr.wolfdev.wolfanime.domain.repository

import fr.wolfdev.wolfanime.config.DatabaseFactory.dbQuery
import fr.wolfdev.wolfanime.domain.MediaTitle
import fr.wolfdev.wolfanime.utils.AnilistMediaTitle
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

internal object MediaTitleTable : IntIdTable() {
    val romaji = varchar("romaji", 255)
    val english = varchar("english", 255).nullable()
    val native = varchar("native", 255)

    fun toDomain(row: ResultRow) = MediaTitle(row[id].value, row[romaji], row[english], row[native])
}

class MediaTitleRepository {
    init {
        transaction {
            SchemaUtils.create(MediaTitleTable)
        }
    }

    suspend fun addMediaTitle(romaji: String, english: String, native: String): MediaTitle = dbQuery {
        MediaTitleTable.insert {
            it[this.romaji] = romaji
            it[this.english] = english
            it[this.native] = native
        }.resultedValues?.map { MediaTitleTable.toDomain(it) }?.first()!!
    }

    suspend fun addBatchInsertMediaTitle(mediaTitles: List<AnilistMediaTitle?>): List<MediaTitle> = dbQuery {
        MediaTitleTable.batchInsert(mediaTitles) {
            this[MediaTitleTable.romaji] = it?.romaji ?: "romaji"
            this[MediaTitleTable.english] = it?.english
            this[MediaTitleTable.native] = it?.native ?: "native"
        }.map { MediaTitleTable.toDomain(it) }
    }

    suspend fun getMediaTitle(romaji: String): MediaTitle? = dbQuery {
        MediaTitleTable.selectAll()
            .where { MediaTitleTable.romaji eq romaji }
            .map { MediaTitleTable.toDomain(it) }.firstOrNull()
    }

    suspend fun getAllMediaTitle(romajiList: List<String>): List<MediaTitle?> = dbQuery {
        MediaTitleTable.selectAll()
            .where { MediaTitleTable.romaji inList romajiList }
            .map { MediaTitleTable.toDomain(it) }
    }
}
