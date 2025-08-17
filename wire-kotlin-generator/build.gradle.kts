
plugins {
    id("java-library")
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    kotlin("jvm")
}

repositories {
    mavenCentral()
    google()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    api(libs.wire.schema)
    implementation(libs.wire.runtime)
    implementation(libs.wire.grpc.client)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinpoet)
    implementation(libs.okio)
    implementation(libs.mcp.kotlin)
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.ktor.client)
    implementation(libs.ktor.server.core)
}
