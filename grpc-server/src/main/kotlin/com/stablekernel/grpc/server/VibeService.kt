package com.stablekernel.grpc.server

import examples.v1.Example.SetVibeRequest
import examples.v1.Example.SetVibeResponse
import examples.v1.VibeServiceGrpcKt
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.modelcontextprotocol.kotlin.sdk.GetPromptRequest
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.mcpSseTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class VibeService : VibeServiceGrpcKt.VibeServiceCoroutineImplBase() {
    val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val requestBuilder: HttpRequestBuilder.() -> Unit = {
        this.url.host = "localhost"
        this.url.port = 2345
    }
    val transport =
        HttpClient(CIO) {
            applyDefaults()
        }.mcpSseTransport(
            urlString = "http://localhost:2345",
            reconnectionTime = 120.seconds,
            requestBuilder = requestBuilder,
        )
    val client =
        Client(
            Implementation(
                name = "protoc-gen-kotlin-mcp-server",
                version = "1.0.",
            ),
        )

    init {
        coroutineScope.launch {
            client.connect(transport)
        }
    }

    private var previousVibe: String = ""

    override suspend fun setVibe(request: SetVibeRequest): SetVibeResponse {
        client.ping()
        val prompt = client.getPrompt(GetPromptRequest("VibeRequest", arguments = mapOf("vibe" to request.vibe)))
        val vibePrompt = prompt?.messages?.map { it.content.toString() }?.first() ?: "No vibe prompt obtained"
        val vibeRegex = vibePrompt.replace(Regex(".*vibe=(.*)}\\.\\)"), "$1")
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
        return examples.v1.Example.GetVibeResponse.newBuilder().setVibe(previousVibe).build()
    }
}
