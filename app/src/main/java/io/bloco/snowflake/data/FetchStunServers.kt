package io.bloco.snowflake.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber

class FetchStunServers(
    private val httpClientProvider: () -> HttpClient,
) {
    suspend operator fun invoke(): List<String>? {
        val response = try {
            httpClientProvider().get(URL).body<Response>()
        } catch (e: Exception) {
            Timber.w(e, "Could not fetch STUN servers")
            return null
        }

        val entry = response.snowflake.firstOrNull() ?: run {
            Timber.w("Could not fetch STUN servers: empty response")
            return null
        }

        val params = entry.split(Regex("\\s"))
        val ice = params.firstOrNull { it.startsWith(ICE_PREFIX) }?.drop(ICE_PREFIX.length) ?: run {
            Timber.w("Could not fetch STUN servers: ice param not found")
            return null
        }

        val urls = ice.split(",").filter { it.startsWith(STUN_PREFIX) }

        if (urls.isEmpty()) {
            Timber.w("Could not fetch STUN servers: ice param without STUN servers")
            return null
        }

        return urls
    }

    @Serializable
    private data class Response(
        @SerialName("snowflake") val snowflake: List<String>,
    )

    companion object {
        private const val URL = "https://bridges.torproject.org/moat/circumvention/builtin"
        private const val ICE_PREFIX = "ice="
        private const val STUN_PREFIX = "stun:"
    }
}