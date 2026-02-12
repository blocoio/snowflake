package io.bloco.snowflake.models

sealed interface Capacity {
    val value: Long

    data class Specific(override val value: Long) : Capacity

    data object Unlimited : Capacity {
        override val value = 0L
    }

    companion object {
        fun fromValue(value: Long?) =
            if (value != null && value <= 0L) Unlimited else Specific(value ?: 1)
    }
}