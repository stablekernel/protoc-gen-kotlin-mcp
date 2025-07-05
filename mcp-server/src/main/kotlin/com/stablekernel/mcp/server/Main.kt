package com.stablekernel.mcp.server

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ReadResourceResult
import io.modelcontextprotocol.kotlin.sdk.Resource
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities.Prompts
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities.Resources
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities.Tools
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.TextResourceContents
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.RegisteredResource
import io.modelcontextprotocol.kotlin.sdk.server.RegisteredTool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcp
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

fun main(args: Array<String>) {
    println("Starting Topeka MCP Server...")
    EngineMain.main(args)
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
                            tools = Tools(listChanged = true),
                            prompts = Prompts(listChanged = true),
                            resources = Resources(subscribe = true, listChanged = true),
                        ),
                ),
        )
    val setVibeRequestResource =
        RegisteredResource(
            resource =
                Resource(
                    uri = "proto://SetVibeRequest",
                    name = "SetVibeRequest",
                    description = "Protocol buffer message for SetVibeRequest",
                    mimeType = "application/json",
                ),
            readHandler = { request ->
                ReadResourceResult(
                    contents =
                        listOf(
                            TextResourceContents(
                                text = "This is the SetVibeRequest resource.",
                                uri = request.uri,
                                mimeType = "application/json",
                            ),
                        ),
                )
            },
        )
    val setVibeRequestTool =
        RegisteredTool(
            tool =
                Tool(
                    name = "SetVibeRequest",
                    description = "Set the server vibe",
                    inputSchema = Tool.Input(JsonObject(mapOf("vibe" to JsonPrimitive("vibe parameter"))), required = listOf("vibe")),
                ),
            handler = { request ->
                CallToolResult(
                    content =
                        listOf(
                            TextContent(request.arguments?.get("vibe")?.toString() ?: "Unknown vibe"),
                        ),
                )
            },
        )
    val getVibeRequestResource =
        RegisteredResource(
            resource =
                Resource(
                    uri = "proto://GetVibeRequest",
                    name = "GetVibeRequest",
                    description = "Protocol buffer message for GetVibeRequest",
                    mimeType = "application/json",
                ),
            readHandler = { request ->
                ReadResourceResult(
                    contents =
                        listOf(
                            TextResourceContents(
                                text = "This is the GetVibeRequest resource.",
                                uri = request.uri,
                                mimeType = "application/json",
                            ),
                        ),
                )
            },
        )
    val getVibeRequestTool =
        RegisteredTool(
            tool =
                Tool(
                    name = "GetVibeRequest",
                    description = "Get the server vibe",
                    inputSchema = Tool.Input(JsonObject(mapOf("vibe" to JsonPrimitive("vibe parameter"))), required = listOf("vibe")),
                ),
            handler = { request ->
                CallToolResult(
                    content =
                        listOf(
                            TextContent((request.arguments?.get("vibe") ?: "Unknown vibe").toString()),
                        ),
                )
            },
        )
    server.onInitalized {
        println("Server initialized with: ${server.clientVersion?.name} v${server.clientVersion?.version}")
        server.addResources(listOf(setVibeRequestResource, getVibeRequestResource))
        server.addTools(listOf(setVibeRequestTool, getVibeRequestTool))
    }
    mcp {
        server
    }
}
