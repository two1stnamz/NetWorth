package com.maroondevelopment.networth.data

import kotlinx.serialization.Serializable

@Serializable
data class QuoteDto(
    val ticker: String,
    val price: Double,
    val change: Double,
    /** The instrument's display name, or `null` where the service reports none. */
    val name: String? = null
)
