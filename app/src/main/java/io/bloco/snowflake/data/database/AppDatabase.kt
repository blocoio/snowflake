package io.bloco.snowflake.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.bloco.snowflake.models.DayStats
import timber.log.Timber
import java.io.File

@Database(
    entities = [DayStats::class],
    version = 1,
)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun statsDao(): StatsDao

    fun clear() {
        try {
            if (isOpen) close()
            openHelper.readableDatabase.path?.let { File(it).delete() }
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete database")
        }
    }
}
