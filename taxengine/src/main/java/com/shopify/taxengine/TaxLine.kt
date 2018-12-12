package com.shopify.taxengine

import java.math.BigDecimal

data class TaxLine(
    val itemKey: String,
    val taxRateKey: String,
    var amount: BigDecimal
) {
}
