package com.shopify.taxengine

import com.shopify.taxengine.rule.LuxuryTaxRule
import org.junit.Test

import org.junit.Assert.*


class LuxuryTaxRuleTests {

    @Test
    fun `no luxury tax under limit`() {
        val taxRate = TaxRate(key = "tax:state", rate = "0.1", zone = TaxRate.Zone.PROVINCE)
        val item = Sale(key = "item", quantity = 1, unitPrice = "1.0")
        val taxes = Taxes(currency = USD, taxRates = listOf(taxRate), taxableItems = listOf(item), location = massachusetts(), taxRules = listOf(
            LuxuryTaxRule(exemptItems = setOf(item.key))
        ))
        assertTrue(taxes.taxLines.isEmpty())
    }

    @Test
    fun `tax applies over threshold`() {
        val taxRate = TaxRate(key = "tax:state", rate = "0.1", zone = TaxRate.Zone.PROVINCE)
        val item = Sale(key = "item", quantity = 1, unitPrice = "200")
        val taxes = Taxes(currency = USD, taxRates = listOf(taxRate), taxableItems = listOf(item), location = massachusetts(), taxRules = listOf(
            LuxuryTaxRule(exemptItems = setOf(item.key))
        ))
        assertTaxRate(taxes = taxes, rate = taxRate, amount = "2.5")
    }

    @Test
    fun `tax applied to non-exempt item`() {
        val taxRate = TaxRate(key = "tax:state", rate = "0.1", zone = TaxRate.Zone.PROVINCE)
        val item = Sale(key = "item", quantity = 1, unitPrice = "200")
        val taxes = Taxes(currency = USD, taxRates = listOf(taxRate), taxableItems = listOf(item), location = massachusetts(), taxRules = listOf(
            LuxuryTaxRule(exemptItems = setOf("different-item"))
        ))
        assertTaxRate(taxes = taxes, rate = taxRate, amount = "20")
    }

    @Test
    fun `tax not applied to invalid zone`() {
        val taxRate = TaxRate(key = "tax:state", rate = "0.1", zone = TaxRate.Zone.COUNTRY)
        val item = Sale(key = "item", quantity = 1, unitPrice = "200")
        val taxes = Taxes(currency = USD, taxRates = listOf(taxRate), taxableItems = listOf(item), location = massachusetts(), taxRules = listOf(
            LuxuryTaxRule(exemptItems = setOf(item.key))
        ))
        assertTaxRate(taxes = taxes, rate = taxRate, amount = "20")
    }
    
}