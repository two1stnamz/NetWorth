package com.maroondevelopment.networth.data.datasource

import com.maroondevelopment.networth.data.QuoteDto

interface QuoteDataSource {

    suspend fun getQuote(symbol: String): QuoteDto
}

class RemoteQuoteDataSource : QuoteDataSource {

    override suspend fun getQuote(symbol: String): QuoteDto {
        // Simulate fetching quote data from a remote API
        return QuoteDto(
            name = "Example Company",
            symbol = symbol,
            price = 150.0, // Example price
            change = 1.5, // Example change
        )
    }
}