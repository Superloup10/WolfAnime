package fr.wolfdev.wolfanime.config

import fr.wolfdev.wolfanime.domain.repository.*
import fr.wolfdev.wolfanime.domain.service.*
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

object ModulesConfig {
    private val userModule = DI.Module("USER") {
        bind<UserService>() with singleton { UserService(instance()) }
        bind<UserRepository>() with singleton { UserRepository() }
    }
    private val mediaTitleModule = DI.Module("MEDIATITLE") {
        bind<MediaTitleService>() with singleton { MediaTitleService(instance()) }
        bind<MediaTitleRepository>() with singleton { MediaTitleRepository() }
    }
    private val crunchyrollModule = DI.Module("CRUNCHYROLL") {
        bind<CrunchyrollService>() with singleton { CrunchyrollService(instance()) }
        bind<CrunchyrollRepository>() with singleton { CrunchyrollRepository() }
    }
    private val mediaModule = DI.Module("MEDIA") {
        bind<MediaService>() with singleton { MediaService(instance()) }
        bind<MediaRepository>() with singleton { MediaRepository() }
    }
    private val mediaListModule = DI.Module("MEDIALIST") {
        bind<MediaListService>() with singleton { MediaListService(instance()) }
        bind<MediaListRepository>() with singleton { MediaListRepository() }
    }

    internal val kodein = DI {
        import(userModule)
        import(mediaTitleModule)
        import(crunchyrollModule)
        import(mediaModule)
        import(mediaListModule)
    }
}
