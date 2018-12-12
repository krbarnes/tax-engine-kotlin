package com.shopify.taxengine.rule

import com.shopify.taxengine.Location
import com.shopify.taxengine.TaxRate
import com.shopify.taxengine.TaxableItem
import java.math.BigDecimal

data class DisabledTaxRule(val tax: String, val disabledItems: Set<String>): TaxRule {
    override val key: String = "rule:builtin:disabled"

    override fun appliesTo(taxableItem: TaxableItem, location: Location, taxRate: TaxRate): Boolean {
        return this.tax == taxRate.key && disabledItems.contains(taxableItem.key)
    }

    override fun taxableAmountFor(taxableItem: TaxableItem, location: Location, taxRate: TaxRate): BigDecimal {
        return BigDecimal.ZERO
    }

    override fun taxRateFor(taxableItem: TaxableItem, location: Location, taxRate: TaxRate): BigDecimal {
        return BigDecimal.ZERO
    }
}