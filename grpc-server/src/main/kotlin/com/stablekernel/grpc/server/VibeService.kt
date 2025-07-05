package com.stablekernel.grpc.server

import examples.v1.Example.SetVibeRequest
import examples.v1.Example.SetVibeResponse
import examples.v1.VibeServiceGrpcKt
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.modelcontextprotocol.kotlin.sdk.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.mcpSseTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.time.Duration.Companion.seconds

class VibeService(
    private val client: Client =
        Client(
            Implementation(
                name = "protoc-gen-kotlin-mcp-server",
                version = "1.0.",
            ),
        ),
    coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    private val clientUrlString: String = "http://localhost:2345",
) : VibeServiceGrpcKt.VibeServiceCoroutineImplBase() {
    val requestBuilder: HttpRequestBuilder.() -> Unit = {
        this.url.host = "localhost"
        this.url.port = 2345
    }
    val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }
    var transport =
        HttpClient(CIO) {
            applyDefaults(json = json)
        }.mcpSseTransport(
            urlString = clientUrlString,
            reconnectionTime = 15.seconds,
            requestBuilder = requestBuilder,
        )

    init {
        coroutineScope.launch {
            try {
                println("Connecting to MCP server...")
                client.connect(transport)
            } catch (e: Exception) {
                println("Failed to connect to MCP server: ${e.message}")
            }
        }
    }

    private var previousVibe: String = ""

    override suspend fun setVibe(request: SetVibeRequest): SetVibeResponse {
        tryPingReconnectOnFailure()
        val toolResult =
            client.callTool(
                CallToolRequest("SetVibeRequest", arguments = JsonObject(mapOf("vibe" to JsonPrimitive(request.vibe)))),
            )
        val vibeContent = toolResult?.content?.map { it.toString() }?.first() ?: "No vibe prompt obtained"
        val vibeRegex =
            vibeContent
                .replace(Regex("TextContent\\(text=(.*)\\)"), "$1")
                .replace("\\\"", "")
                .replace("\"", "")
        val response =
            SetVibeResponse
                .newBuilder()
                .setVibe(vibeRegex)
                .setPreviousVibe(previousVibe)
                .build()
        previousVibe = vibeRegex
        return response
    }

    override suspend fun getVibe(request: examples.v1.Example.GetVibeRequest): examples.v1.Example.GetVibeResponse {
        tryPingReconnectOnFailure()
        val toolResult =
            client.callTool(
                CallToolRequest(name = "GetVibeRequest", arguments = JsonObject(mapOf("vibe" to JsonPrimitive(previousVibe)))),
            )
        val vibeContent = toolResult?.content?.map { it.toString() }?.firstOrNull() ?: "No vibe prompt obtained"
        val vibeRegex =
            vibeContent
                .replace(Regex("TextContent\\(text=(.*)\\)"), "$1")
                .replace("\\\"", "")
                .replace("\"", "")
        return examples.v1.Example.GetVibeResponse.newBuilder().setVibe(vibeRegex).build()
    }

    private suspend fun tryPingReconnectOnFailure() {
        try {
            client.ping()
        } catch (e: Exception) {
            println("Failed to ping MCP server: ${e.message}")
            try {
                transport.close()
                client.close()
                transport =
                    HttpClient(CIO) {
                        applyDefaults()
                    }.mcpSseTransport(
                        urlString = clientUrlString,
                        reconnectionTime = 15.seconds,
                        requestBuilder = requestBuilder,
                    )
                client.connect(transport)
            } catch (e: Exception) {
                println("Failed to reconnect to MCP server: ${e.message}")
            }
        }
    }
}
