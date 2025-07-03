package com.stablekernel.mcp.server

import io.ktor.server.application.Application
import io.modelcontextprotocol.kotlin.sdk.GetPromptResult
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.PromptArgument
import io.modelcontextprotocol.kotlin.sdk.PromptMessage
import io.modelcontextprotocol.kotlin.sdk.Role.user
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities.Prompts
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities.Resources
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcp

fun main(args: Array<String>) {
    println("Starting Topeka MCP Server...")
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val server =
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
                            prompts = Prompts(listChanged = null),
                            resources = Resources(subscribe = null, listChanged = null),
                        ),
                ),
        )
    server.onInitalized {
        println("Server initialized with: ${server.clientVersion?.name} v${server.clientVersion?.version}")
        server.addPrompt(
            name = "VibeRequest",
            description = "The Topeka Vibe",
            arguments =
                listOf(
                    PromptArgument(
                        name = "vibe",
                        description = "The vibe in Topeka",
                        required = true,
                    ),
                ),
            promptProvider = { request ->
                GetPromptResult(
                    request.name,
                    listOf(
                        PromptMessage(user, TextContent("The vibe in Topeka is ${request.arguments}.")),
                    ),
                )
            },
        )
    }
    mcp {
        server
    }
}
