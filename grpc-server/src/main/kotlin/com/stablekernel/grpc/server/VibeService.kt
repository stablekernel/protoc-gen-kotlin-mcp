package com.stablekernel.grpc.server

import examples.v1.Example.SetVibeRequest
import examples.v1.Example.SetVibeResponse
import examples.v1.VibeServiceGrpcKt

class VibeService : VibeServiceGrpcKt.VibeServiceCoroutineImplBase() {
    override suspend fun setVibe(request: SetVibeRequest): SetVibeResponse {
        return SetVibeResponse.newBuilder().setVibe(request.vibe).build()
    }

    override suspend fun getVibe(request: examples.v1.Example.GetVibeRequest): examples.v1.Example.GetVibeResponse {
        return examples.v1.Example.GetVibeResponse.newBuilder().setVibe("Good Vibes Only").build()
    }
}
