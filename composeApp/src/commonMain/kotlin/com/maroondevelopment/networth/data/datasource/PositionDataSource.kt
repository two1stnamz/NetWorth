package com.maroondevelopment.networth.data.datasource

import com.maroondevelopment.networth.Database
import com.maroondevelopment.networth.data.QuoteDto
import com.maroondevelopment.networth.data.model.PositionModel

interface PositionDataSource {

    suspend fun getPositions(accountId: String): List<PositionModel>

    suspend fun insertOrUpdate(positionModel: PositionModel)

    suspend fun deletePosition(positionModel: PositionModel)
}

class PositionDataSourceImpl(
    private val database: Database
) : PositionDataSource {

    override suspend fun getPositions(accountId: String): List<PositionModel> {
        try {
            database.positionQueries.selectBySymbol(symbol).executeAsOne().apply {
                quote = QuoteDto(
                    ticker = this.symbol,
                    price = this.price,
                    change = this.change
                )
            }
        } catch (t: Throwable) {
            println("[BENG][LocalQuoteDataSource] getQuote() - EXCEPTION!!! ${t.message}")
        }

        return emptyList()
    }

    override suspend fun insertOrUpdate(positionModel: PositionModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePosition(positionModel: PositionModel) {
        TODO("Not yet implemented")
    }
}