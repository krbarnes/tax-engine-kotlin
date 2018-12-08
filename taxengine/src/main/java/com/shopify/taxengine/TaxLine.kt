package com.shopify.taxengine

import java.math.BigDecimal

data class TaxLine(val item: String,
                   val tax: String,
                   var amount: BigDecimal
) {
}
