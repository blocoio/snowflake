package io.bloco.snowflake.models

data class SnowflakeConfig(
    val capacity: Capacity,
    val brokerUrl: String = "https://snowflake-broker.torproject.net/",
    val relayUrl: String = "wss://snowflake.bamsoftware.com",
    val stunServer: String = "stun:stun.epygi.com:3478",
    val natProbeUrl: String = "https://snowflake-broker.torproject.net:8443/probe",
    val pollInterval: Long = 120,
    val proxyTypeIdentifier: String = "bloco",
)