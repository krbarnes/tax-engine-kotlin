package com.shopify.taxengine

import java.math.BigDecimal

data class TaxLineFragment(
    val itemKey: String,
    val taxRateKey: String,
    var amount: BigDecimal) {

}