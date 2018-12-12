package com.shopify.taxengine

import com.shopify.taxengine.rule.*
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class Taxes(val currency: Currency,
                 val taxLines: List<TaxLine>) {

    companion object TaxFragmentCalculator {

        fun computeTaxLinesFrom(fragments: List<TaxLineFragment>, currency: Currency): List<TaxLine> {
            var fragmentAggregates: HashMap<String, BigDecimal> = hashMapOf()

            fragments.forEach { taxLine ->
                val currentVal = fragmentAggregates[taxLine.taxRateKey] ?: BigDecimal.ZERO
                fragmentAggregates[taxLine.taxRateKey] = currentVal + taxLine.amount
            }

            var taxLines: List<TaxLine> = fragments.map { TaxLine(itemKey = it.itemKey, taxRateKey = it.taxRateKey, amount = it.amount) }
            fragmentAggregates.keys.forEach { key ->
                val aggregate = fragmentAggregates[key]!!.setScale(currency.minorUnits, RoundingMode.HALF_UP)
                taxLines = addressRoundingErrors(taxRateKey = key, currency = currency, aggregate = aggregate, taxLines = taxLines)
            }

            taxLines.forEach { taxLine ->
                taxLine.amount = taxLine.amount.setScale(currency.minorUnits, RoundingMode.HALF_UP)
            }

            return taxLines
        }


        private fun addressRoundingErrors(taxRateKey: String, currency: Currency, aggregate: BigDecimal, taxLines: List<TaxLine>): List<TaxLine> {
            if (currency.minorUnits <= 0) { return taxLines }
            val linesForTaxFromLineItem = taxLines.filter { it.taxRateKey == taxRateKey }
            var difference = aggregate - linesForTaxFromLineItem.fold(BigDecimal.ZERO) {  acc, taxLine ->
                acc + taxLine.amount.setScale(currency.minorUnits, RoundingMode.HALF_UP)
            }

            val toTake = (if (difference.greaterThan(BigDecimal.ZERO)) BigDecimal.ONE else BigDecimal.ONE.negate()).divide(BigDecimal.TEN.pow(currency.minorUnits))

            var i = 0
            while (difference.comparesNotEqual(BigDecimal.ZERO)) {
                taxLines[i].amount += toTake
                difference -= toTake
                i += 1
                if (i == taxLines.count()) {
                    i = 0
                }
            }
            return taxLines
        }

        fun calculateFragments(currency: Currency,
                                       taxRates: List<TaxRate>,
                                       taxesIncluded: Boolean,
                                       taxableItems: List<TaxableItem>,
                                       location: Location,
                                       taxRules: List<TaxRule>): List<TaxLineFragment> {

            var fragments: HashMap<String, List<TaxLineFragment>> = hashMapOf()
            taxableItems.forEach { taxableItem ->
                var totalTax: BigDecimal = BigDecimal.ZERO

                var taxLines: List<TaxLineFragment> = taxRates.mapNotNull { taxRate ->
                    val rule = taxRuleFor(taxableItem, taxRate, location, taxRules)
                    val taxLine = makeTaxLineFragmentFor(taxableItem, taxRate, rule, location, totalTax)
                    totalTax += taxLine?.amount ?: BigDecimal.ZERO
                    taxLine
                }

                if (taxesIncluded) {
                    taxLines = adjustForTaxesIncluded(taxLines, taxableItem)
                }
                fragments[taxableItem.key] = taxLines
            }
            return fragments.flatMap { it.value }
        }

        private fun makeTaxLineFragmentFor(taxableItem: TaxableItem, taxRate: TaxRate, taxRule: TaxRule, location: Location, totalTax: BigDecimal): TaxLineFragment? {
            val taxableAmount = taxRule.taxableAmountFor(taxableItem, location, taxRate) + (if (taxRate.type == TaxRate.ApplicationType.COMPOUND) totalTax else BigDecimal.ZERO)
            val tax = taxRule.taxRateFor(taxableItem, location, taxRate)
            val taxAmount = tax.multiply(taxableAmount)
            if (taxAmount.lessThan(BigDecimal.ZERO) || taxAmount.comparesEqual(BigDecimal.ZERO)) { return null }
            return TaxLineFragment(taxableItem.key, taxRate.key, taxAmount)
        }

        private fun taxRuleFor(taxableItem: TaxableItem, taxRate: TaxRate, location: Location, rules: List<TaxRule>): TaxRule {
            return rules.firstOrNull {
                it.appliesTo(taxableItem, location, taxRate)
            } ?: DefaultRule()
        }

        private fun adjustForTaxesIncluded(taxLineFragments: List<TaxLineFragment>, taxableItem: TaxableItem): List<TaxLineFragment> {
            val totalTax = taxLineFragments.fold(BigDecimal.ZERO) { acc, taxLineFragment ->
                acc + taxLineFragment.amount
            }

            val taxableAmount = taxableItem.taxableAmount
            val taxablePlusTax = taxableAmount + totalTax

            if (taxablePlusTax.greaterThan(BigDecimal.ZERO)) {
                val scalingFactor = taxableAmount.divide(taxablePlusTax, MathContext.DECIMAL128)
                taxLineFragments.forEach {
                    it.amount = it.amount * scalingFactor
                }
            }

            return taxLineFragments
        }
    }

    constructor(currency: Currency, taxRates: List<TaxRate>, taxesIncluded: Boolean = false, taxableItems: List<TaxableItem>, location: Location, taxRules: List<TaxRule> = listOf())
            : this(
        currency = currency,
        taxLines = Taxes.computeTaxLinesFrom(fragments = Taxes.calculateFragments(currency, taxRates, taxesIncluded, taxableItems, location, taxRules), currency = currency)
    )

    /// Total tax by tax rate.
    val collectedTaxes: Map<String, BigDecimal>
        get() {
            var collected: HashMap<String, BigDecimal> = hashMapOf()
             taxLines.forEach { taxLine ->
                 val currentVal = collected[taxLine.taxRateKey] ?: BigDecimal.ZERO
                 collected[taxLine.taxRateKey] = currentVal + taxLine.amount
            }
            return collected
        }

    val itemizedTaxes: Map<String, List<TaxLine>>
        get() {
            var itemized: HashMap<String, List<TaxLine>> = hashMapOf()
            taxLines.forEach { taxLine ->
                val currentVal = itemized[taxLine.itemKey] ?: listOf()
                val mutableVal = currentVal.toMutableList()
                mutableVal.add(taxLine)
                itemized[taxLine.itemKey] = mutableVal.toList()
            }
            return itemized
        }

}