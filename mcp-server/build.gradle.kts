@file:Suppress("UnstableApiUsage")

plugins {
    application
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    alias(libs.plugins.ktor)
}

group = "com.stablekernel.mcp"
val versionInput: String? = findProperty("version") as String?

version = versionInput ?: "1.0-SNAPSHOT"

application {
    mainClass = "com.stablekernel.mcp.server.MainKt"
    applicationDefaultJvmArgs = listOf("-Dapp.version=$version")
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.server)
    implementation(libs.logback.classic)
    implementation(libs.mcp.kotlin)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter(libs.versions.junit)
            dependencies {
                implementation(libs.kotest)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.ktor.server.test.host)
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("src/main/kotlin")
        }
    }
}

ktor {
    fatJar {
        archiveFileName.set("protoc-gen-kotlin-mcp-server-$version.jar")
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}
