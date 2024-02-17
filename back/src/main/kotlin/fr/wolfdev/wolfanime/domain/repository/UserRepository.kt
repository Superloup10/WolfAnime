package fr.wolfdev.wolfanime.domain.repository

import fr.wolfdev.wolfanime.config.DatabaseFactory.dbQuery
import fr.wolfdev.wolfanime.domain.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

internal object Users : Table() {
    val id = integer("id")
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)

    fun toDomain(row: ResultRow) = User(row[id], row[name])
}

class UserRepository {
    init {
        transaction {
            SchemaUtils.create(Users)
        }
    }

    suspend fun addUser(id: Int, name: String): User = dbQuery {
        Users.insert {
            it[this.id] = id
            it[this.name] = name
        }.resultedValues?.map { Users.toDomain(it) }?.first()!!
    }

    suspend fun getUser(name: String): User? = dbQuery {
        Users.selectAll().where { Users.name eq name }.map { Users.toDomain(it) }.firstOrNull()
    }
}
