package com.shopify.taxengine

import java.math.BigDecimal

internal fun <E> Collection<E>.doesNotContain(item: E): Boolean {
    return !this.contains(item)
}

internal fun BigDecimal.lessThan(other: BigDecimal): Boolean {
    return !(this.greaterThan(other) || this.comparesEqual(other))
}

internal fun BigDecimal.greaterThan(other: BigDecimal): Boolean {
    return this.compareTo(other) == 1
}

internal fun BigDecimal.comparesEqual(other: BigDecimal): Boolean {
    return this.compareTo(other) == 0
}

internal fun BigDecimal.comparesNotEqual(other: BigDecimal): Boolean {
    return !this.comparesEqual(other)
}
