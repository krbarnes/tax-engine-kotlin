package com.shopify.taxengine.rule

import com.shopify.taxengine.Location
import com.shopify.taxengine.TaxRate
import com.shopify.taxengine.TaxableItem
import java.math.BigDecimal

interface TaxRule {
    val key: String

    fun appliesTo(taxableItem: TaxableItem, location: Location, taxRate: TaxRate): Boolean

    fun taxableAmountFor(taxableItem: TaxableItem, location: Location, taxRate: TaxRate): BigDecimal {
        return taxableItem.taxableAmount
    }

    fun taxRateFor(taxableItem: TaxableItem, location: Location, taxRate: TaxRate): BigDecimal {
        return taxRate.rate
    }
}