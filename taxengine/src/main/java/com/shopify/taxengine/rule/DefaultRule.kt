package com.shopify.taxengine.rule

import com.shopify.taxengine.*


// TaxRule should be a data class with these values??

class DefaultRule: TaxRule {
    override val key: String = "rule:builtin:default"

    override fun appliesTo(taxableItem: TaxableItem, location: Location, taxRate: TaxRate): Boolean {
        return true
    }
}