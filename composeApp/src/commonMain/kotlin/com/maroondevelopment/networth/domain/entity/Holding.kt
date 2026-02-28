package com.maroondevelopment.networth.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Holding(
    val name: String,
    val symbol: String,
    val quantity: Double,
)
