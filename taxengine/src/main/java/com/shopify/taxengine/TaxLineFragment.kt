package com.shopify.taxengine

import java.math.BigDecimal

data class TaxLineFragment(val item: String,
                           val taxRateKey: String,
                           var amount: BigDecimal) {

}