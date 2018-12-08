package com.shopify.taxengine.rule

import com.shopify.taxengine.Location
import com.shopify.taxengine.TaxRate
import com.shopify.taxengine.TaxableItem
import java.math.BigDecimal

data class DisabledTaxRule(val tax: String, val disabledItems: Set<String>): TaxRule {
    override val key: String = "rule:builtin:disabled"

    override fun appliesTo(lineItem: TaxableItem, location: Location, tax: TaxRate): Boolean {
        return this.tax == tax.key && disabledItems.contains(lineItem.key)
    }

    override fun taxableAmountFor(lineItem: TaxableItem, location: Location, tax: TaxRate): BigDecimal {
        return BigDecimal.ZERO
    }

    override fun taxRateFor(lineItem: TaxableItem, location: Location, tax: TaxRate): BigDecimal {
        return BigDecimal.ZERO
    }
}