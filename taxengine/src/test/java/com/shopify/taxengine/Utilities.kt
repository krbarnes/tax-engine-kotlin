package com.shopify.taxengine

import junit.framework.Assert.failNotEquals
import org.junit.Assert
import java.math.BigDecimal

val CAD = Currency(code = "CAD", minorUnits = 2)
val USD = Currency(code = "USD", minorUnits = 2)
val GBP = Currency(code = "GBP", minorUnits = 2)
val JPY = Currency(code = "JPY", minorUnits = 0)

fun massachusetts(county: String = "", city: String = "", postalCode: String = ""): Location {
    return Location(
        countryCode = "US",
        countryName = "United States",
        provinceCode = "MA",
        provinceName = "Massachusetts",
        county = county,
        city = city,
        postalCode = postalCode
    )
}

fun newyork(county: String = "", city: String = "", postalCode: String = ""): Location {
    return Location(
        countryCode = "US",
        countryName = "United States",
        provinceCode = "NY",
        provinceName = "New York",
        county = county,
        city = city,
        postalCode = postalCode
    )
}

val OTTAWA = Location(countryCode = "CA", countryName = "Canada", provinceCode = "ON", provinceName = "Ontario", county = "", city = "Ottawa", postalCode = "K2P 0R4")
val LONDON = Location(countryCode = "UK", countryName = "United Kingdom", provinceCode = "", provinceName = "", county = "", city = "London", postalCode = "WC2N 5DU")
val TOKYO = Location(countryCode = "JP", countryName = "Japan", provinceCode = "", provinceName = "", county = "", city = "Tokyo", postalCode = "863-1201")

fun assertTaxRate(taxes: Taxes, rate: TaxRate, amount: String) {
    val actual = taxes.collectedTaxes[rate.key]!!
    val expected = BigDecimal(amount)

    if (expected.compareTo(actual) == 0) {
        return
    } else {
        failNotEquals("Computed taxes are not equal.", expected, actual)
    }
}