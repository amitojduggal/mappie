plugins {
    alias(libs.plugins.kotlin.jvm) version embeddedKotlinVersion
    alias(libs.plugins.gradle.plugin.publish)
}

dependencies {
    compileOnly(libs.kotlin.gradle.plugin.api)
}

gradlePlugin {
    plugins {
        create("mappie") {
            id = "io.github.mappie"
            displayName = "Mappie Gradle Plugin"
            description = "Kotlin compiler plugin for generating object mappers"
            implementationClass = "io.github.mappie.MappieGradlePlugin"
        }
    }
}