package fr.wolfdev.wolfanime.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.io.File

object DatabaseFactory {
    private fun createHikariDataSource(url: String, driver: String, user: String, pass: String) = HikariDataSource(
        HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            username = user
            password = pass
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            validate()
        }
    )

    fun init(config: ApplicationConfig) {
        val driverClassName = config.property("storage.driverClassName").getString()
        val jdbcUrl = buildString {
            append(config.property("storage.jdbcURL").getString())
            append(config.propertyOrNull("storage.dbFilePath")?.getString()?.let {
                File(it).canonicalFile.absolutePath
            } ?: "")
            append(config.propertyOrNull("storage.connectionParameters")?.getString() ?: "")
        }
        val username = config.property("storage.username").getString()
        val password = config.property("storage.password").getString()
        Database.connect(createHikariDataSource(jdbcUrl, driverClassName, username, password))
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}
