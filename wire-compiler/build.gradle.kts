// import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    kotlin("jvm")
    id(libs.plugins.serialization.get().pluginId)
}

application {
    mainClass.set("com.squareup.wire.WireCompiler")
}

repositories {
    mavenCentral()
    google()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(project(":wire-kotlin-generator"))
    implementation(libs.wire.java.generator)
    implementation(libs.wire.swift.generator)
    implementation(libs.okio)
    implementation(libs.okio.fakefilesystem)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.charles.kaml)
}
