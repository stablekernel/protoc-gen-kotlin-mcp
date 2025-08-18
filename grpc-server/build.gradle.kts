@file:Suppress("UnstableApiUsage")

import com.google.protobuf.gradle.id


plugins {
    application
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.ktor)
}

group = "com.stablekernel.grpc"
val versionInput: String? = findProperty("version") as String?

version = versionInput ?: "1.0-SNAPSHOT"

application {
    mainClass = "com.stablekernel.grpc.server.MainKt"
    applicationDefaultJvmArgs = listOf("-Dapp.version=$version")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/krpc/grpc")
    google()
}

dependencies {
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.server)
    implementation(libs.logback.classic)
    implementation(libs.grpc.netty)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.services)
    implementation(libs.kotlinx.grpc)
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

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.58.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

sourceSets {
    main {
        kotlin {
            srcDir("src/main/kotlin")
            srcDir("build/generated/source/proto/main/kotlin")
            srcDir("build/generated/source/proto/main/grpckt")
        }
        java {
            srcDir("build/generated/source/proto/main/grpc")
            srcDir("build/generated/source/proto/main/java")
        }
    }
}

ktor {
    fatJar {
        archiveFileName.set("protoc-gen-kotlin-grpc-server-$version.jar")
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

ktlint {
    filter {
        exclude { element ->
            val path = element.file.path
            path.contains("/generated/")
        }
    }
}
