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
            val stunServer =
                if (stunServers.isNullOrEmpty()) {
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
        private val DEFAULT_STUN_SERVERS =
            listOf(
                "stun:stun.epygi.com:3478",
                "stun:stun.uls.co.za:3478",
                "stun:stun.voipgate.com:3478",
                "stun:stun.mixvoip.com:3478",
                "stun:stun.telnyx.com:3478",
                "stun:stun.hot-chilli.net:3478",
                "stun:stun.fitauto.ru:3478",
                "stun:stun.m-online.net:3478",
            )
    }
}
