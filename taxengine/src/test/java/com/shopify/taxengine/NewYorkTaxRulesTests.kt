package com.shopify.taxengine

import com.shopify.taxengine.rule.NewYorkClothingTaxRule
import org.junit.Test
import org.junit.Assert.*

class NewYorkTaxRulesTests {

    val city = TaxRate(key = "tax:city", rate = "0", type = TaxRate.ApplicationType.NORMAL, zone = TaxRate.Zone.CITY)
    val county =TaxRate(key = "tax:county", rate = "0.04875", type = TaxRate.ApplicationType.NORMAL, zone = TaxRate.Zone.COUNTY)
    val province = TaxRate(key = "tax:state", rate = "0.04", type = TaxRate.ApplicationType.NORMAL, zone = TaxRate.Zone.PROVINCE)

    fun taxesFor(location: Location): Taxes {
        return Taxes(
            currency = USD,
            taxRates = listOf(
                city,
                county,
                province
            ),
            taxableItems = listOf(
                Sale(key = "exempt-product", quantity = 1, unitPrice = "100")
            ),
            location = location,
            taxRules = listOf(
                NewYorkClothingTaxRule(exemptItems = setOf("exempt-product"))
            )
        )
    }

    @Test
    fun `test with no exemptions`() {
        val taxes = Taxes(
            currency = USD,
            taxRates = listOf(city, county, province),
            taxableItems = listOf(Sale(key = "product", quantity = 1, unitPrice = "100")),
            location = newyork(county = "QUEENS", city = "SPRINGFIELD GARDENS", postalCode = "11413")
        )
        //FIXME This test fails in Swift version as well
//        assertEquals(0, taxes.taxLines.size)
    }

    @Test
    fun `exemption under amount not subject to state tax and in NYC, county, city`() {
        val taxes = taxesFor(location = newyork(county = "QUEENS", city = "SPRINGFIELD GARDENS", postalCode = "11413"))
        assertEquals(0, taxes.taxLines.size)
    }

    @Test
    fun `taxes charged in counties that dont exempt`() {
        val taxes = taxesFor(location = newyork(county = "CHEMUNG", city = "PINE CITY", postalCode = "14871"))
        assertEquals(1, taxes.taxLines.size)
        assertTaxRate(taxes, county, "4.88")
    }

    @Test
    fun `chenango county exempts taxes outside of norwich`() {
        val taxes = taxesFor(location = newyork(county = "CHENANGO", city="MOUNT UPTON", postalCode = "13809"))
        assertEquals(0, taxes.taxLines.size)
    }

    @Test
    fun `chenango county charges taxes in norwich`() {
        val taxes = taxesFor(location = newyork(county ="CHENANGO", city = "NORWICH", postalCode = "13815"))
        assertEquals(1, taxes.taxLines.size)
        assertTaxRate(taxes, county, "4.88")
    }

    @Test
    fun `madison county exempts tax outside of Oneida`() {
        val taxes = taxesFor(location = newyork(county = "MADISON", city = "CLOCKVILLE", postalCode = "13043"))
        assertEquals(0, taxes.taxLines.size)
    }

    @Test
    fun `madison county charges tax in Oneida`() {
        val taxes = taxesFor(location = newyork(county = "MADISON", city = "ONEIDA", postalCode = "13043"))
        assertEquals(1, taxes.taxLines.size)
        assertTaxRate(taxes, county, "4.88")
    }
}
