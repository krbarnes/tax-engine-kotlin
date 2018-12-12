package com.shopify.taxengine.rule

import com.shopify.taxengine.*
import java.math.BigDecimal

data class NewYorkClothingTaxRule(val exemptItems: Set<String>): TaxRule {

    override val key: String = "rule:builtin:newyork:clothing"

    private val exemptCounties: Set<String> = setOf(
        "NEW YORK",
        "BRONX",    // the bronx
        "QUEENS",   // queens
        "KINGS",    // brooklyn
        "RICHMOND", // staten island
        "WAYNE",
        "TIOGA",
        "HAMILTON",
        "GREENE",
        "DELAWARE",
        "COLUMBIA",
        "CHAUTAUQUA"
    )

    private val threshold: BigDecimal = BigDecimal(110.0)

    override fun appliesTo(taxableItem: TaxableItem, location: Location, taxRate: TaxRate): Boolean {
        if (exemptItems.doesNotContain(taxableItem.key)) { return false }
        if (location.countryCode != "US") { return false }
        if (location.provinceCode != "NY") { return false }
        if (taxRate.zone == TaxRate.Zone.PROVINCE || (taxRate.zone == TaxRate.Zone.COUNTY && isCountyExempt(location))) { return true }
        return false
    }

    private fun isCountyExempt(location: Location): Boolean {
        if (exemptCounties.contains(location.county)) {
            return true
        }
        if (location.county == "CHENANGO") {
            return location.city != "NORWICH"
        }
        if (location.county == "MADISON") {
            return location.city != "ONEIDA"
        }
        return false
    }


    override fun taxableAmountFor(taxableItem: TaxableItem, location: Location, taxRate: TaxRate): BigDecimal {
        if (taxableItem.taxableAmount.lessThan(taxableItem.quantity.multiply(threshold))) {
            return BigDecimal.ZERO
        }
        return taxableItem.taxableAmount
    }

}

