package com.shopify.taxengine.rule

import com.shopify.taxengine.Location
import com.shopify.taxengine.TaxRate
import com.shopify.taxengine.TaxableItem
import java.math.BigDecimal

interface TaxRule {
    val key: String

    fun appliesTo(lineItem: TaxableItem, location: Location, tax: TaxRate): Boolean

    fun taxableAmountFor(lineItem: TaxableItem, location: Location, tax: TaxRate): BigDecimal {
        return lineItem.taxableAmount
    }

    fun taxRateFor(lineItem: TaxableItem, location: Location, tax: TaxRate): BigDecimal {
        return tax.rate
    }
}