package io.bloco.snowflake.common

import kotlin.math.pow

fun convertToBytes(bytes: Long, unit: String?): Long {
    if (unit == null) return bytes
    val units = listOf("B", "KB", "MB", "GB", "TB")
    val baseExp = units.indexOf(unit.uppercase()).coerceAtLeast(0)
    return bytes * (1024.toDouble().pow(baseExp)).toLong()
}