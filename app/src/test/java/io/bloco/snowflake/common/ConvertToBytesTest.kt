package io.bloco.snowflake.common

import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertToBytesTest {
    @Test
    fun test() {
        assertEquals(0, convertToBytes(bytes = 0, unit = null))
        assertEquals(1, convertToBytes(bytes = 1, unit = "B"))
        assertEquals(1024, convertToBytes(bytes = 1, unit = "KB"))
        assertEquals(1024 * 1024, convertToBytes(bytes = 1, unit = "MB"))
        assertEquals(100 * 1024 * 1024, convertToBytes(bytes = 100, unit = "MB"))
    }
}
