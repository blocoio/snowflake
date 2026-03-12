package io.bloco.snowflake.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.bloco.snowflake.models.DayStats

@Database(
    entities = [DayStats::class],
    version = 1,
)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun statsDao(): StatsDao
}