package com.shopify.taxengine

import java.math.BigDecimal

data class TaxRate(val key: String,
                   val rate: BigDecimal,
                   val type: ApplicationType,
                   val zone: Zone) {

    enum class ApplicationType {
        NORMAL,
        COMPOUND
    }

    enum class Zone {
        COUNTRY,
        PROVINCE,
        COUNTY,
        CITY,
        CUSTOM
    }

    constructor(key: String, rate: String, type: ApplicationType = ApplicationType.NORMAL, zone: Zone = Zone.COUNTRY):
            this(key = key, rate = BigDecimal(rate), type = type, zone = zone)

}
