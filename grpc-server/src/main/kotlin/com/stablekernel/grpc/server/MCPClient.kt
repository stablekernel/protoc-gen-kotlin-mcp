package com.stablekernel.grpc.server

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRetryDelayContext
import io.ktor.client.plugins.HttpRetryShouldRetryContext
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.request.HttpRequest
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode.Companion.TooManyRequests
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun HttpClientConfig<*>.applyDefaults(
    json: Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        },
    loggingConfig: LoggingConfig = LoggingConfig(),
    retryConfig: RetryConfig = RetryConfig(),
    timeoutConfig: TimeoutConfig = TimeoutConfig(),
    hostConfig: HostConfig = HostConfig(),
) {
    installContentNegotiation(json)
    installLogging(loggingConfig)
    installRetry(retryConfig)
    installTimeout(timeoutConfig)
    install(Resources)
    install(SSE)
    configureDefaultRequest(hostConfig)
}

fun HttpClientConfig<*>.installContentNegotiation(json: Json) {
    install(ContentNegotiation) {
        json(json)
    }
}

fun HttpClientConfig<*>.installLogging(config: LoggingConfig) {
    if (config.enabled) {
        install(Logging) {
            logger = config.logger
            level = config.level
        }
    }
}

fun HttpClientConfig<*>.installRetry(config: RetryConfig) {
    if (config.enabled) {
        install(HttpRequestRetry) {
            maxRetries = config.maxRetries
            retryOnException()
            retryIf(block = config.retryIf)
            exponentialDelay(
                baseDelayMs = 10_000,
                maxDelayMs = 60_000,
            )
            modifyRequest { it.headers.append("X_RETRY_COUNT", retryCount.toString()) }
        }
    }
}

fun HttpClientConfig<*>.installTimeout(config: TimeoutConfig) {
    if (config.enabled) {
        install(HttpTimeout) {
            requestTimeoutMillis = config.requestTimeoutMillis
            connectTimeoutMillis = config.connectTimeoutMillis
            socketTimeoutMillis = config.socketTimeoutMillis
        }
    }
}

fun HttpClientConfig<*>.configureDefaultRequest(config: HostConfig) {
    defaultRequest {
        url {
            protocol = config.protocol
            host = config.hostname
            port = config.port
        }
    }
}

data class LoggingConfig(
    val enabled: Boolean = false,
    val logger: Logger = Logger.DEFAULT,
    val level: LogLevel = LogLevel.INFO,
)

data class RetryConfig(
    val enabled: Boolean = true,
    val maxRetries: Int = 3,
    val retryIf: HttpRetryShouldRetryContext.(HttpRequest, HttpResponse) -> Boolean = { _, response ->
        // Retry on per minute rate limit but not on daily rate limit
        val rateLimitExceeded = response.headers["x-ratelimit-remaining"]?.toInt()?.let { it == 0 } ?: false
        val serverError = response.status.value in 500..599
        serverError || (response.status == TooManyRequests && !rateLimitExceeded)
    },
    val delayMillis: HttpRetryDelayContext.(Int) -> Long = { retry -> retry * 1500L },
)

data class TimeoutConfig(
    val enabled: Boolean = true,
    val requestTimeoutMillis: Long = 300_000,
    val connectTimeoutMillis: Long = 300_000,
    val socketTimeoutMillis: Long = 300_000,
)

data class HostConfig(
    val protocol: URLProtocol = URLProtocol.HTTP,
    var hostname: String = "localhost",
    val port: Int = 2345,
)
