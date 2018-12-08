package com.shopify.taxengine.rule

import com.shopify.taxengine.Location
import com.shopify.taxengine.TaxRate
import com.shopify.taxengine.TaxableItem
import com.shopify.taxengine.doesNotContain
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

    private val threshold: BigDecimal = BigDecimal(100.0)

    override fun appliesTo(lineItem: TaxableItem, location: Location, tax: TaxRate): Boolean {
        if (exemptItems.doesNotContain(lineItem.key)) { return false }
        if (location.countryCode != "US") { return false }
        if (location.provinceCode != "NY") { return false }
        if (tax.zone != TaxRate.Zone.PROVINCE || (tax.zone == TaxRate.Zone.COUNTY && isCountyExempt(location))) { return false }
        return true
    }

    private fun isCountyExempt(location: Location): Boolean {
        // this is some kind of bullshit.
        // thanks Obama.
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
}

