import io.gitlab.arturbosch.detekt.Detekt
import nl.littlerobots.vcu.plugin.versionCatalogUpdate

plugins {
    application
    jacoco
    alias(libs.plugins.gradle.version)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.dependency.analysis)

    alias(libs.plugins.kotlin)

    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.sonarqube)
}

apply(from = "gradle/sonar.gradle")

group = "fr.wolfdev"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

ktlint {
    version.set("1.1.1")
    ignoreFailures.set(true)
    // additionalEditorconfigFile.set(file(".editorconfig"))
}

detekt {
    toolVersion = libs.versions.detekt.get()
    source.from(files("src/main/kotlin"))
    config.from(files("detekt-config.yml"))
    buildUponDefaultConfig = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        xml {
            outputLocation.set(file("${layout.buildDirectory}/reports/detekt/detekt.xml"))
            required.set(true)
        }
    }
}

tasks.dokkaHtml.configure {
    outputDirectory.set(layout.buildDirectory.asFile.get().resolve("dokka"))
}

tasks.jacocoTestReport {
    sourceDirectories.from(sourceSets["main"].kotlin.srcDirs)
    dependsOn(detekt)
}

sonar {
    properties {
        property("sonar.kotlin.detekt.reportPaths", detekt.reportsDir)
    }
}

project.tasks["check"].dependsOn(tasks.jacocoTestReport)

dependencyAnalysis {
    issues {
        all {
            onAny {
                exclude("org.jetbrains.kotlin:kotlin-stdlib")
            }
        }
    }
    structure {
        bundle("dependencies-transitive") {
            includeGroup("org.jetbrains.kotlin")
            includeGroup("org.jetbrains.kotlinx")
            includeGroup("io.ktor")
            includeGroup("junit")
            includeGroup("org.slf4j")
            includeGroup("com.squareup.okhttp3")
            includeGroup("com.expediagroup")
        }
    }
}

versionCatalogUpdate {
    sortByKey = false
}
