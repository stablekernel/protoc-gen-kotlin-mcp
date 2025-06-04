package com.stablekernel.grpc.server

import io.grpc.protobuf.services.ProtoReflectionService
import io.ktor.server.engine.embeddedServer

fun main() {
  embeddedServer(GRpc, configure = {
    port = 2345
    serverConfigurer = {
      addService(VibeService())
      addService(ProtoReflectionService.newInstance())
    }
  }) {
  }.start(wait = true)
}

private fun isJUnitTest(): Boolean {
  return try {
    Class.forName("org.junit.jupiter.api.Test")
    true
  } catch (e: ClassNotFoundException) {
    false
  }
}
