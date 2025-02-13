package tech.mappie.testing

import org.gradle.testkit.runner.GradleRunner
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*

enum class KotlinPlatform { JVM, MULTIPLATFORM }

abstract class TestBase {

    @TempDir
    protected lateinit var directory: File

    protected lateinit var runner: GradleRunner

    protected open val platform: KotlinPlatform = KotlinPlatform.JVM

    protected open val gradleVersion: String? = null

    protected open val kotlinVersion = "2.1.0"

    @BeforeEach
    fun setup() {
        runner = GradleRunner.create().apply {
            forwardOutput()
            withProjectDir(directory)
            gradleVersion?.let { withGradleVersion(gradleVersion) }
        }

        gradleVersion?.let { println("Using Gradle version $it") }
        println("Using Kotlin version $kotlinVersion")

        kotlin("settings.gradle.kts",
            """
            pluginManagement {
                repositories {
                    mavenLocal()
                    gradlePluginPortal()
                }
             }
            """.trimIndent()
        )

        when (platform) {
            KotlinPlatform.JVM -> jvm()
            KotlinPlatform.MULTIPLATFORM -> multiplatform()
        }
    }

    protected fun kotlin(file: String, @Language("kotlin") code: String) {
        directory.resolve(file).apply {
            appendText(code)
        }
    }

    protected fun java(file: String, @Language("java") code: String) {
        directory.resolve(file).apply {
            appendText(code)
        }
    }

    private fun jvm() {
        directory.resolve("src/main/kotlin").mkdirs()
        directory.resolve("src/main/java").mkdirs()
        directory.resolve("src/test/kotlin").mkdirs()

        kotlin("build.gradle.kts",
            """
            plugins {
                id("org.jetbrains.kotlin.jvm") version "$kotlinVersion"
                id("tech.mappie.plugin") version "$version"
            }
            repositories {
                mavenLocal()
                mavenCentral()
            }

            dependencies {
                testImplementation(kotlin("test"))
            }

            tasks.test {
                useJUnitPlatform()
                testLogging {
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL       
                }
            }

            """.trimIndent()
        )
    }

    private fun multiplatform() {
        directory.resolve("src/commonMain/kotlin").mkdirs()
        directory.resolve("src/commonTest/kotlin").mkdirs()
        directory.resolve("src/jvmMain/kotlin").mkdirs()
        directory.resolve("src/jvmTest/kotlin").mkdirs()
        directory.resolve("src/mingwX64Main/kotlin").mkdirs()
        directory.resolve("src/mingwX64Test/kotlin").mkdirs()

        kotlin("build.gradle.kts",
            """
            plugins {
                id("org.jetbrains.kotlin.multiplatform") version "$kotlinVersion"
                id("tech.mappie.plugin") version "$version"
            }

            repositories {
                mavenLocal()
                mavenCentral()
            }

            kotlin {
                applyDefaultHierarchyTemplate()
                
                sourceSets {
                    val commonTest by getting {
                        dependencies {
                            implementation(kotlin("test"))
                            implementation("tech.mappie:mappie-api:1.0.0")
                        }
                    }
                    mingwX64Main.dependencies {
                        implementation("tech.mappie:mappie-api:1.0.0")
                    }
                }
            
                jvm()
                mingwX64()
            }
            """.trimIndent()
        )
    }

    companion object {
        private val version = javaClass.classLoader.getResourceAsStream("mappie.properties").use {
            Properties().apply { load(it) }.getProperty("VERSION")
        }

        @BeforeAll
        @JvmStatic
        fun start() {
            println("Using mappie version $version")
        }
    }
}