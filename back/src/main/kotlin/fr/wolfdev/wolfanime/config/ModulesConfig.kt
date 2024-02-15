package fr.wolfdev.wolfanime.config

import fr.wolfdev.wolfanime.domain.repository.UserRepository
import fr.wolfdev.wolfanime.domain.service.UserService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

object ModulesConfig {
    private val userModule = DI.Module("USER") {
        bind<UserService>() with singleton { UserService(instance()) }
        bind<UserRepository>() with singleton { UserRepository() }
    }

    internal val kodein = DI {
        import(userModule)
    }
}
