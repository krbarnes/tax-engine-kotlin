package com.shopify.taxengine

//Swift uses UInt8 for minorUnits, could Short instead of Int
data class Currency(val code: String, val minorUnits: Int) {
}