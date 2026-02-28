package com.maroondevelopment.networth.data.datasource

import com.maroondevelopment.networth.data.HoldingDto

interface HoldingDataSource {

    suspend fun getHoldings(): List<HoldingDto>

}


class LocalHoldingDataSource : HoldingDataSource {

    override suspend fun getHoldings(): List<HoldingDto> {
        return listOf(
            HoldingDto("AAPL", 10.0),
            HoldingDto("GOOGL", 5.0),
            HoldingDto("AMZN", 2.0)
        )
    }
}