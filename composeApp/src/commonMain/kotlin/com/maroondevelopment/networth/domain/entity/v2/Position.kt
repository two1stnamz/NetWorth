package com.maroondevelopment.networth.domain.entity.v2

import com.maroondevelopment.networth.domain.entity.Account

data class Position(
    val totalValue: Double,
    val accounts: List<Account>
)
