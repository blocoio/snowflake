package io.bloco.snowflake.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import io.bloco.snowflake.models.DayStats
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface StatsDao {
    @Query("SELECT * FROM day_stats WHERE date = :date LIMIT 1")
    fun getByDate(date: LocalDate): Flow<DayStats?>

    @Upsert
    fun insertOrUpdate(dayStats: DayStats)
}
