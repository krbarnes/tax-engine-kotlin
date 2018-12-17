package com.shopify.taxengine

import org.junit.Test

class TaxEngineTests {

    @Test
    fun `basic taxes`() {
        val hst = TaxRate(key = "tax:province:hst", rate = "0.13", zone = TaxRate.Zone.PROVINCE)
        val lineItem = Sale(key = "Misc", quantity = 1, unitPrice = "1.00")
        val taxes = Taxes(currency = CAD, taxRates = listOf(hst), location = OTTAWA, taxableItems = listOf(lineItem))

        assertTaxRate(taxes, hst, "0.13")
    }

    @Test
    fun `test rounding is done correctly`() {
        val tax = TaxRate(key = "tax:country:gst", rate = "0.07", type = TaxRate.ApplicationType.NORMAL, zone = TaxRate.Zone.COUNTRY)
        val taxableItems = listOf(
            Sale(key = "custom1", quantity = 1, unitPrice = "0.49"),
            Sale(key = "custom2", quantity = 1, unitPrice = "0.49")
        )
        val taxes = Taxes(currency = CAD, taxRates = listOf(tax), taxableItems = taxableItems, location = OTTAWA)
        assertTaxRate(taxes = taxes, rate = tax, amount = "0.07")
    }

    @Test
    fun `zero decimal currency should not have decimal`() {
        val tax = TaxRate(key = "tax:country:gst", rate = "0.07", zone = TaxRate.Zone.COUNTRY)
        val taxableItems = listOf(
            Sale(key = "custom:1", quantity = 1, unitPrice = "10")
        )
        val taxes = Taxes(currency = JPY, taxRates = listOf(tax), taxableItems = taxableItems, location = TOKYO)
        assertTaxRate(taxes = taxes, rate = tax, amount = "1")
    }

    @Test
    fun `compound taxes work too right?`() {
        val gst = TaxRate(key = "tax:federal:gst", rate = "0.05")
        val qst = TaxRate(key = "tax:province:qst", rate = "0.095", type = TaxRate.ApplicationType.COMPOUND)
        val lineItem = Sale(key = "Misc", quantity = 1, unitPrice = "100.00")
        val taxes = Taxes(currency = CAD, taxRates = listOf(gst, qst), taxableItems = listOf(lineItem), location = OTTAWA)

        assertTaxRate(taxes = taxes, rate = gst, amount = "5")
        assertTaxRate(taxes = taxes, rate = qst, amount = "9.98")
    }

    @Test
    fun `taxes extracted when they are already included`() {
        val vat = TaxRate(key = "tax:vat", rate = "0.2")
        val lineItem = Sale(key = "Misc", quantity = 1, unitPrice = "100")
        val taxes = Taxes(currency = GBP, taxRates = listOf(vat), taxesIncluded = true, taxableItems = listOf(lineItem), location = LONDON)
        assertTaxRate(taxes = taxes, rate = vat, amount = "16.67")
    }

    @Test
    fun `penny distribution with zero price line item`() {
        val taxRate = TaxRate(key = "tax:country:gst", rate = "0.07", type = TaxRate.ApplicationType.NORMAL, zone = TaxRate.Zone.COUNTRY)
        val taxableItems = listOf(
            Sale(key = "custom:1", quantity = 1, unitPrice = "0.0"),
            Sale(key = "custom:2", quantity = 1, unitPrice = "0.49"),
            Sale(key = "custom:3", quantity = 1, unitPrice = "0.49")
        )
        val taxes = Taxes(currency = CAD, taxRates = listOf(taxRate), taxableItems = taxableItems, location = OTTAWA)

        assertBigDecimalEquals("0.04", taxes.taxLines("custom:2").first().amount)
        assertBigDecimalEquals("0.03", taxes.taxLines("custom:3").first().amount)
        assertTaxRate(taxes = taxes, rate = taxRate, amount = "0.07")
    }

    @Test
    fun `pennies distributed acroll line items correctly`() {
        val taxableItems = (0 until 10).map {
            Sale(key = "custom $it", quantity = 1, unitPrice = "0.46")
        }
        val tax = TaxRate(key = "tax:country:gst", rate = "0.08", type = TaxRate.ApplicationType.NORMAL, zone = TaxRate.Zone.COUNTRY)
        val taxes = Taxes(currency = CAD, taxRates = listOf(tax), taxableItems = taxableItems, location = OTTAWA)
        assertBigDecimalEquals("0.37", taxes.amount("tax:country:gst"))
        (0 until 3).forEach {
            assertBigDecimalEquals("0.03", taxes.amount(taxableItemKey = "custom $it", taxRateKey = "tax:country:gst"))
        }
        (3 until 10).forEach {
            assertBigDecimalEquals("0.04", taxes.amount(taxableItemKey = "custom $it", taxRateKey = "tax:country:gst"))
        }
    }

}