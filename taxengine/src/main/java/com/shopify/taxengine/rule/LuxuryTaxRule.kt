package com.shopify.taxengine.rule

import com.shopify.taxengine.Location
import com.shopify.taxengine.TaxRate
import com.shopify.taxengine.TaxableItem
import com.shopify.taxengine.doesNotContain
import java.math.BigDecimal

data class LuxuryTaxRule(val exemptItems: Set<String>) : TaxRule {

    override val key: String = "rule:builtin:luxury"

    private val thresholds: HashMap<String, BigDecimal> = hashMapOf(
        "MA" to BigDecimal(175.0),
        "RI" to BigDecimal(250.0)
    )

    override fun taxableAmountFor(taxableItem: TaxableItem, location: Location, taxRate: TaxRate): BigDecimal {
        val threshold = thresholds[location.provinceCode]
        return if (threshold == null) {
            taxableItem.taxableAmount
        } else {
            val computed = taxableItem.taxableAmount - (threshold * taxableItem.quantity)
            computed.max(BigDecimal.ZERO)
        }
    }

    override fun appliesTo(taxableItem: TaxableItem, location: Location, taxRate: TaxRate): Boolean {
        if (location.countryCode != "US") { return false }
        if (taxRate.zone != TaxRate.Zone.PROVINCE) { return false }
        if (thresholds[location.provinceCode] == null) { return false }
        if (exemptItems.doesNotContain(taxableItem.key)) { return false }
        return true
    }
}