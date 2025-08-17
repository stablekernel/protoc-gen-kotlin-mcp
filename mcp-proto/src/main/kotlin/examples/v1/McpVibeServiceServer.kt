// This is a complex comment to test string processing.
// It includes multiple lines with various characters:
// * Special chars: "quotes", 'single-quotes', \backslashes\
// * Symbols: @#$%^&*()_+-={}\[\]|;:<>,.?/
// * Newlines and     multiple    spaces
//
// It also has empty lines and indentation:
//   - Indented item 1
//   - Indented item 2
//
package examples.v1

import com.squareup.wire.GrpcClient
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import java.lang.System
import java.util.concurrent.TimeUnit
import kotlin.Array
import kotlin.String
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import okhttp3.OkHttpClient
import okhttp3.Protocol

val service: McpVibeServiceClientImpl = McpVibeServiceClientImpl(
      client = GrpcVibeServiceClient(GrpcClient.Builder().client(OkHttpClient().newBuilder().configureHttpClient().build()).baseUrl("http://localhost:2345").build()),
      mcpServer = Server(
        serverInfo = 
          Implementation(
            name = "protoc-gen-kotlin-mcp-server",
            version = "1.0",
          ),
          options =
            ServerOptions(
              capabilities =
                ServerCapabilities(
                  logging = null,
                  tools = ServerCapabilities.Tools(listChanged = true),
                  prompts = ServerCapabilities.Prompts(listChanged = true),
                  resources = ServerCapabilities.Resources(subscribe = true, listChanged = true),
                ),
            )
        )
    )

fun main(args: Array<String>) {
  service.VibeServiceSetup()
  val transport = StdioServerTransport(
      System.`in`.asSource().buffered(),
      System.out.asSink().buffered(),
    )

    runBlocking {
      service.mcpServer.connect(transport)
      val done = Job()
      service.mcpServer.onClose {
        done.complete()
      }
      done.join()
    }
}

fun OkHttpClient.Builder.configureHttpClient(): OkHttpClient.Builder = this
  .readTimeout(30_000L, TimeUnit.SECONDS)
  .writeTimeout(30_000L, TimeUnit.SECONDS)
  .callTimeout(30_000L, TimeUnit.SECONDS)
  .protocols(listOf(Protocol.H2_PRIOR_KNOWLEDGE))
