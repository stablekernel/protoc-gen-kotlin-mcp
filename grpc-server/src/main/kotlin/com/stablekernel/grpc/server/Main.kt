package com.stablekernel.grpc.server

import io.grpc.protobuf.services.ProtoReflectionService
import io.ktor.server.engine.embeddedServer
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.client.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

fun main() {
    val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val client = Client(
        Implementation(
            name = "protoc-gen-kotlin-mcp-server",
            version = "1.0.",
        ),
    )
    embeddedServer(GRpc, configure = {
        port = 2349
        serverConfigurer = {
            addService(VibeService(client = client, coroutineScope = coroutineScope))
            addService(ProtoReflectionService.newInstance())
        }
    }) {
    }.start(wait = true)
    coroutineScope.launch {
        println("Closing MCP client connection...")
        client.close()
    }
}
