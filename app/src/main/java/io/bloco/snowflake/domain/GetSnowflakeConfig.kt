package io.bloco.snowflake.domain

import io.bloco.snowflake.models.Capacity
import io.bloco.snowflake.models.SnowflakeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetSnowflakeConfig(
    private val getCapacity: () -> Flow<Capacity>,
    private val getStunServers: () -> Flow<List<String>?>,
) {
    operator fun invoke() =
        combine(
            getCapacity(),
            getStunServers(),
        ) { capacity, stunServers ->
            val stunServer = if (stunServers.isNullOrEmpty()) {
                DEFAULT_STUN_SERVERS
            } else {
                stunServers
            }.random()

            SnowflakeConfig(
                capacity = capacity,
                stunServer = stunServer,
            )
        }

    companion object {
        private val DEFAULT_STUN_SERVERS = listOf(
            "stun.epygi.com:3478",
            "stun.uls.co.za:3478",
            "stun.voipgate.com:3478",
            "stun.mixvoip.com:3478",
            "stun.nextcloud.com:3478",
            "stun.bethesda.net:3478",
            "stun.nextcloud.com:443",
        )
    }
}