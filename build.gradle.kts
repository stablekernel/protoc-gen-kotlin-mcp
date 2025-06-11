plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.1.20"
    id ("com.google.protobuf") version "0.9.4"
}

group = "com.stablekernel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.google.protobuf:protobuf-java:4.28.2")
    implementation("com.google.protobuf:protobuf-java-util:4.31.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}