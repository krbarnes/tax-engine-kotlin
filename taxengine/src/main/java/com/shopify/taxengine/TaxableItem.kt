package com.shopify.taxengine

import java.math.BigDecimal

interface TaxableItem {
    val key: String //Swift uses custom type
    val taxableAmount: BigDecimal
    val quantity: BigDecimal
}
