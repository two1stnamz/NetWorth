package com.maroondevelopment.networth.domain.entity

data class Position(
    val ticker: String,
    val units: Double,
    val tickerValue: Double,
    val totalValue: Double
)
