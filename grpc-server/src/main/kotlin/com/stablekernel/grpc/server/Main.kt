package com.stablekernel.grpc.server

import io.grpc.protobuf.services.ProtoReflectionService
import io.ktor.server.engine.embeddedServer

fun main() {
    embeddedServer(GRpc, configure = {
        port = 2349
        serverConfigurer = {
            addService(VibeService())
            addService(ProtoReflectionService.newInstance())
        }
    }) {
    }.start(wait = true)
}
