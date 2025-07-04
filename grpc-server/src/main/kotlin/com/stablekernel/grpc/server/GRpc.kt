package com.stablekernel.grpc.server

import com.stablekernel.grpc.server.GRpcApplicationEngine.Configuration
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionServiceV1
import io.ktor.events.Events
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.application.ApplicationStopPreparing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ApplicationEngineFactory
import io.ktor.server.engine.BaseApplicationEngine
import java.util.concurrent.TimeUnit

object GRpc : ApplicationEngineFactory<GRpcApplicationEngine, Configuration> {
    override fun configuration(configure: Configuration.() -> Unit): Configuration {
        return Configuration().apply(configure)
    }

    override fun create(
        environment: ApplicationEnvironment,
        monitor: Events,
        developmentMode: Boolean,
        configuration: Configuration,
        applicationProvider: () -> Application,
    ): GRpcApplicationEngine {
        return GRpcApplicationEngine(environment, monitor, developmentMode, configuration)
    }
}

class GRpcApplicationEngine(
    environment: ApplicationEnvironment,
    monitor: Events,
    developmentMode: Boolean,
    configure: Configuration,
) : BaseApplicationEngine(
        environment,
        monitor,
        developmentMode,
    ) {
    class Configuration : BaseApplicationEngine.Configuration() {
        var port: Int = 2349

        var serverConfigurer: ServerBuilder<*>.() -> Unit = {}
    }

    private val configuration = configure
    private var server: Server? = null

    override fun start(wait: Boolean): ApplicationEngine {
        server =
            ServerBuilder
                .forPort(configuration.port)
                .apply(configuration.serverConfigurer)
                .addService(ProtoReflectionServiceV1.newInstance())
                .build()

        server?.start()

        if (wait) {
            server?.awaitTermination()
        }

        return this
    }

    override fun stop(
        gracePeriodMillis: Long,
        timeoutMillis: Long,
    ) {
        environment.monitor.raise(ApplicationStopPreparing, environment)

        server?.awaitTermination(gracePeriodMillis, TimeUnit.MILLISECONDS)

        if (server != null) {
            server?.shutdownNow()
        }
    }
}
