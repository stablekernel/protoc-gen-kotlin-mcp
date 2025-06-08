package com.stablekernel.mcp.server

import io.ktor.server.application.Application
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities.Prompts
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities.Resources
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcp

fun main(args: Array<String>) {
    println("Starting Topeka MCP Server...")
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    mcp {
        Server(
            serverInfo =
                Implementation(
                    name = "protoc-gen-kotlin-mcp-server",
                    version = "1.0",
                ),
            options =
                ServerOptions(
                    capabilities =
                        ServerCapabilities(
                            prompts = Prompts(listChanged = true),
                            resources = Resources(subscribe = true, listChanged = true),
                        ),
                ),
        )
    }
}
