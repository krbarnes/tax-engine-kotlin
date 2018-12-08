package com.shopify.taxengine

import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal

class ExtensionTests {

    @Test
    fun `1 is greater than 0`() {
        assertTrue(BigDecimal.ONE.greaterThan(BigDecimal.ZERO))
    }

    @Test
    fun `1 is not greater than 1`() {
        assertFalse(BigDecimal.ONE.greaterThan(BigDecimal.ONE))
    }

    @Test
    fun `0 is not greater than 1`() {
        assertFalse(BigDecimal.ZERO.greaterThan(BigDecimal.ONE))
    }

    @Test
    fun `1 is equal to 1`() {
        assertTrue(BigDecimal.ONE.comparesEqual(BigDecimal.ONE))
    }

    @Test
    fun `1 is not equal to 0`() {
        assertFalse(BigDecimal.ONE.comparesEqual(BigDecimal.ZERO))
    }

    @Test
    fun `5 is equal to 5 decimal 00`() {
        assertTrue(BigDecimal(5).comparesEqual(BigDecimal(5.00)))
        assertTrue(BigDecimal(5).equals(BigDecimal(5.00)))
    }
}