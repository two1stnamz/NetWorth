package com.maroondevelopment.networth.domain.repository

import com.maroondevelopment.networth.domain.entity.Holding

interface HoldingsRepository {

    suspend fun getHoldings(): List<Holding>
}