package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.domain.entity.Quote
import com.maroondevelopment.networth.domain.repository.QuoteRepository

class QuoteRepositoryImpl : QuoteRepository {

    override suspend fun getQuote(symbol: String): Quote {
        return Quote(
            name = "",
            symbol = symbol,
            lastPrice = 100.0,
            change = 1.0,
        )
    }
}