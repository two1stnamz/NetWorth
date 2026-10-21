package com.maroondevelopment.networth.domain.entity


data class Quote(
    val symbol: String,
    val price: Double,
    val valueChange: Double,
    /** The instrument's display name, or `null` where the service reports none. */
    val name: String? = null
)
