package com.maroondevelopment.networth.data

import kotlinx.serialization.Serializable

@Serializable
data class QuoteDto(
    val ticker: String,
    val price: Double,
)
