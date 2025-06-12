
plugins {
    id("java-library")
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
    implementation(libs.okio)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinpoet)
}
