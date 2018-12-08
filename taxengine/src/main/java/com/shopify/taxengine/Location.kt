package com.shopify.taxengine

data class Location(val countryCode: String,
                    val countryName: String,
                    val provinceCode: String,
                    val provinceName: String,
                    val county: String,
                    val city: String,
                    val postalCode: String) {

}