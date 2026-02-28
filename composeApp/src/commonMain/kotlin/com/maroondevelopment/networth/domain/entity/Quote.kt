package com.maroondevelopment.networth.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val symbol: String,
    val price: Double,
    val change: Double,
    val volume: Long
)
