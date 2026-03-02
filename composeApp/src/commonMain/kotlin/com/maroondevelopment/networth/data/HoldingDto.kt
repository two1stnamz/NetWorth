package com.maroondevelopment.networth.data

import kotlinx.serialization.Serializable

@Serializable
data class HoldingDto(
    val name: String,
    val symbol: String,
    val quantity: Double,
)
