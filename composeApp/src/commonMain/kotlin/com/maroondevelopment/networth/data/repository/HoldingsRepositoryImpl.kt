package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.domain.entity.Holding
import com.maroondevelopment.networth.domain.repository.HoldingsRepository

class HoldingsRepositoryImpl : HoldingsRepository {

    override suspend fun getHoldings(): List<Holding> {
        return listOf(
            Holding("AAPL", 10.0),
            Holding("GOOGL", 5.0),
            Holding("AMZN", 2.0)
        )
    }
}