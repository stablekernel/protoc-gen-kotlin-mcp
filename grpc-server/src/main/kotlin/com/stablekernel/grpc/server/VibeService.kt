package com.stablekernel.grpc.server

import examples.v1.Example.SetVibeRequest
import examples.v1.Example.SetVibeResponse
import examples.v1.VibeServiceGrpcKt

class VibeService() : VibeServiceGrpcKt.VibeServiceCoroutineImplBase() {
    private var previousVibe: String = ""

    override suspend fun setVibe(request: SetVibeRequest): SetVibeResponse {
        val response = SetVibeResponse.newBuilder().setVibe(request.vibe).setPreviousVibe(previousVibe).build()
        previousVibe = request.vibe
        return response
    }

    override suspend fun getVibe(request: examples.v1.Example.GetVibeRequest): examples.v1.Example.GetVibeResponse {
        return examples.v1.Example.GetVibeResponse.newBuilder().setVibe(previousVibe).build()
    }
}
