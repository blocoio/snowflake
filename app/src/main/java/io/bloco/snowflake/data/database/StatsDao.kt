package io.bloco.snowflake.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.bloco.snowflake.models.DayStats
import kotlinx.coroutines.flow.Flow

@Dao
interface StatsDao {
    @Upsert
    fun insertOrUpdate(dayStats: DayStats)

    @Query("SELECT * FROM day_stats ORDER BY date DESC LIMIT 1")
    fun getLastDate(): Flow<DayStats?>

    @Query("SELECT * FROM day_stats ORDER BY date")
    fun getAll(): Flow<List<DayStats>>
}
