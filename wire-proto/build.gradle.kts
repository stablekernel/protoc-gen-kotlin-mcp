plugins {
    id("org.jetbrains.kotlin.multiplatform")
    alias(libs.plugins.kotlinx.serialization)
    id("jacoco")
}

repositories {
    mavenCentral()
    google()
    maven("https://plugins.gradle.org/m2/")
}

kotlin {
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(libs.wire.runtime)
            implementation(libs.wire.schema)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.okio)
        }
        commonTest.dependencies {}
    }
}