package com.maroondevelopment.networth.domain.entity

data class Quote(
    val name: String,
    val symbol: String,
    val lastPrice: Double,
    val change: Double,
)
