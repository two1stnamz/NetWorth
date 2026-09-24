package com.maroondevelopment.networth.domain.entity.v2

data class Portfolio(
    val totalValue: Double,
    val accounts: List<Account>
)
