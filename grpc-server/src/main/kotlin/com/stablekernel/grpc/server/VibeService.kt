package com.stablekernel.grpc.server

import examples.v1.Example.GetVibeRequest
import examples.v1.Example.GetVibeResponse
import examples.v1.Example.SetVibeArrayRequest
import examples.v1.Example.SetVibeArrayResponse
import examples.v1.Example.SetVibeDetailsResponse
import examples.v1.Example.SetVibeDetailsRequest
import examples.v1.Example.SetVibeObjectsRequest
import examples.v1.Example.SetVibeObjectsResponse
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

    override suspend fun getVibe(request: GetVibeRequest): GetVibeResponse {
        return GetVibeResponse.newBuilder().setVibe(previousVibe).build()
    }

    override suspend fun setVibeDetails(request: SetVibeDetailsRequest): SetVibeDetailsResponse {
        val response = SetVibeDetailsResponse.newBuilder().setVibe(request.vibe).setPreviousVibe(previousVibe).build()
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
