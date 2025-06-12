// import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    kotlin("jvm")
    id (libs.plugins.serialization.get().pluginId)
//  id("com.github.johnrengelman.shadow").apply(false)
}

// if (project.rootProject.name == "wire") {
//  apply(plugin = "com.github.johnrengelman.shadow")
//
//  val shadowJar by tasks.getting(ShadowJar::class) {
//    archiveClassifier.set("jar-with-dependencies")
//  }
// }

application {
    mainClass.set("com.squareup.wire.WireCompiler")
}

repositories {
    mavenCentral()
    google()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation(project(":wire-kotlin-generator"))
    implementation("com.squareup.wire:wire-java-generator:4.9.9")
    implementation("com.squareup.wire:wire-swift-generator:4.9.9")
    implementation("com.squareup.okio:okio:3.11.0")
    implementation("com.squareup.okio:okio-fakefilesystem:3.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
    implementation("com.charleskorn.kaml:kaml:0.73.0")
}
