package com.stablekernel.grpc.server

import com.google.protobuf.kotlin.set
import examples.v1.Example.*
import examples.v1.VibeServiceGrpcKt

class VibeService() : VibeServiceGrpcKt.VibeServiceCoroutineImplBase() {
    private var previousVibe: String = ""

    override suspend fun setVibe(request: SetVibeRequest): SetVibeResponse {
        val response = SetVibeResponse.newBuilder().setVibe(request.vibe).setPreviousVibe(previousVibe).build()
        previousVibe = request.vibe
        return response
    }

    override suspend fun getVibe(request: examples.v1.Example.GetVibeRequest): GetVibeResponse {
        return GetVibeResponse.newBuilder().setVibe(previousVibe).build()
    }

    override suspend fun setVibeDetails(request: SetVibeDetailsRequest): SetVibeResponse {
        val response = SetVibeResponse.newBuilder().setVibe(request.vibe).setPreviousVibe(previousVibe).build()
        previousVibe = request.vibe
        return response
    }

    override suspend fun setVibeArray(request: SetVibeArrayRequest): SetVibeArrayResponse {
        val response = SetVibeArrayResponse.newBuilder().setVibeArray(request.vibeArray).build()
        return response
    }

    override suspend fun setVibeObjects(request: SetVibeObjectsRequest): SetVibeObjectsResponse {
        val response = SetVibeObjectsResponse.newBuilder()
        request.vibeObjectList.forEach { vibeObject ->
            response.addVibeObject(vibeObject)
        }
        return response.build()
    }
}
