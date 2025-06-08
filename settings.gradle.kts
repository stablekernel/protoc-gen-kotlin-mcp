pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/krpc/grpc")
        mavenLocal()
        google()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
include(
    "grpc-server",
    "mcp-server"
)
rootProject.name = "protoc-gen-kotlin-mcp"