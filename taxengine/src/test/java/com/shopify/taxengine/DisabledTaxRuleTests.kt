package com.shopify.taxengine

import com.shopify.taxengine.rule.DisabledTaxRule
import org.junit.Test

import org.junit.Assert.*
import java.math.BigDecimal

class DisabledTaxRuleTests {
    @Test
    fun `rounding is correct`() {
        val gst = TaxRate(key = "tax:country:gst", rate = "0.05", type = TaxRate.ApplicationType.NORMAL, zone = TaxRate.Zone.COUNTRY)
        val pst = TaxRate(key = "tax:country:pst", rate = "0.08", type = TaxRate.ApplicationType.NORMAL, zone = TaxRate.Zone.COUNTRY)
        val federalTaxExemptItem = Sale(key = "item:1", quantity = 1, unitPrice = "100.0")
        val taxableItems: List<Sale> = listOf(
            federalTaxExemptItem,
            Sale(key = "item:2", quantity = 1, unitPrice = "1.0")
        )
        val rule = DisabledTaxRule(tax = gst.key, disabledItems = setOf(federalTaxExemptItem.key))
        val taxes = Taxes(currency = CAD, taxRates = listOf(gst, pst), taxableItems = taxableItems, location = OTTAWA, taxRules = listOf(rule))
        val federalTaxExemptItemTaxLines = taxes.itemizedTaxes["item:1"]
        assertEquals(1, federalTaxExemptItemTaxLines?.size)

        val taxLine = taxes.itemizedTaxes["item:1"]!!.first()
        assertEquals(pst.key, taxLine.tax)
        assertEquals(federalTaxExemptItem.key, taxLine.item)
        assertEquals(0, taxLine.amount.compareTo(BigDecimal(8)))
     }
}