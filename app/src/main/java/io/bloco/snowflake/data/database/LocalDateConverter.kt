package io.bloco.snowflake.data.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateConverter {
    @TypeConverter
    fun toLocalDate(days: String): LocalDate {
        return LocalDate.parse(days)
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}