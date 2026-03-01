package com.maroondevelopment.networth.domain.entity

data class Portfolio(
    val assets: List<Asset>,
    val totalValue: Double
)