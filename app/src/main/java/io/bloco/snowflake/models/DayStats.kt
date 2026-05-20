package io.bloco.snowflake.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity("day_stats")
data class DayStats(
    @PrimaryKey val date: LocalDate = LocalDate.now(),
    @ColumnInfo(name = "connections") val connections: Long = 0L,
    @ColumnInfo(name = "failed_connections") val failedConnections: Long = 0L,
    @ColumnInfo(name = "inbound") val inboundBytes: Long = 0L,
    @ColumnInfo(name = "outbound") val outboundBytes: Long = 0L,
)

fun List<DayStats>.sum(): DayStats? =
    if (isEmpty()) {
        null
    } else {
        DayStats(
            date = first().date,
            connections = sumOf { it.connections },
            failedConnections = sumOf { it.failedConnections },
            inboundBytes = sumOf { it.inboundBytes },
            outboundBytes = sumOf { it.outboundBytes },
        )
    }
