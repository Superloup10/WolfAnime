import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem

plugins {
    application
    jacoco
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktor)
    alias(libs.plugins.graphql)
}

private val hostOs: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()

fun projectFile(path: String): String = projectDir.resolve(path).absolutePath

private val rustLibAbsolutePath: String
    get() = projectFile(
        path = when {
            hostOs.isWindows -> "src/main/rust/target/release/rust_lib.dll"
            else -> "src/main/rust/target/release/librust_lib.so"
        }
    )

/*tasks.register("compileRust", Exec::class) {
    commandLine("cargo", "build", "--release")
    workingDir("src/main/rust")
}

tasks.withType<KotlinCompile> {
    dependsOn("compileRust")
    doLast {
        copy {
            from(rustLibAbsolutePath)
            into("build/libs")
        }
    }
}*/

application {
    applicationDefaultJvmArgs = listOf("-Djava.library.path=$rustLibAbsolutePath")
    mainClass.set("io.ktor.server.netty.EngineMain")
}

ktor {
    val nameProject = projects.wolfAnime.name.lowercase()
    fatJar {
        archiveFileName.set("$nameProject-${projects.wolfAnime.version}-fat.jar")
    }
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.graphql)
    implementation(libs.bundles.databases)

    runtimeOnly(libs.logback)
    runtimeOnly(libs.bundles.databases.runtime)

    detektPlugins(libs.detekt.formating)
    testImplementation(libs.bundles.test)
    testRuntimeOnly(libs.bundles.test.runtime)
}

graphql {
    client {
        endpoint = "https://graphql.anilist.co"
        packageName = "fr.wolfdev.wolfanime.graphql.generated"
        queryFileDirectory = "${project.projectDir}/src/main/resources/queries"
        serializer = GraphQLSerializer.KOTLINX
    }
}
