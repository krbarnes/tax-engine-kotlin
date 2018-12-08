package com.shopify.taxengine

import java.math.BigDecimal

data class Sale(override val key: String, override val taxableAmount: BigDecimal, override val quantity: BigDecimal) : TaxableItem {
    constructor(key: String, quantity: Int, unitPrice: String): this(
        key = key,
        taxableAmount = BigDecimal(quantity) * BigDecimal(unitPrice),
        quantity = BigDecimal(quantity)
    )
}