package io.bloco.snowflake.models

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class SnowflakeConfig(
    val capacity: Capacity,
    val brokerUrl: String = "https://snowflake-broker.torproject.net/",
    val relayUrl: String = "wss://snowflake.bamsoftware.com",
    val stunServer: String = "stun:stun.epygi.com:3478",
    val natProbeUrl: String = "https://snowflake-broker.torproject.net:8443/probe",
    val pollInterval: Duration = 120.seconds,
    val summaryInterval: Duration = 10.seconds,
    val proxyTypeIdentifier: String = "bloco",
)