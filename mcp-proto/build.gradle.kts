plugins {
    application
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    alias(libs.plugins.ktor)
    id("jacoco")
}

val versionInput: String? = findProperty("version") as String?
val version = versionInput ?: "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven("https://plugins.gradle.org/m2/")
}

application {
    mainClass = "examples.v1.McpVibeServiceServerKt"
    applicationDefaultJvmArgs = listOf("-Dapp.version=$version")
}

sourceSets {
    main {
        java {
            srcDir("src/main/kotlin")
        }
    }
}
dependencies {
    implementation(libs.wire.runtime)
    implementation(libs.wire.schema)
    implementation(libs.wire.grpc.client)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.okio)
    implementation(libs.mcp.kotlin)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.server)
    implementation(libs.okhttp)
    implementation(libs.logback.classic)
    implementation(libs.bouncy.castle)
    implementation(libs.bouncy.castle.ssl)
}

ktor {
    fatJar {
        archiveFileName.set("protoc-gen-kotlin-mcp-server-$version.jar")
    }
}
kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}
