package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.data.datasource.QuoteDataSource
import com.maroondevelopment.networth.data.datasource.RemoteQuoteDataSource
import com.maroondevelopment.networth.domain.entity.Quote
import com.maroondevelopment.networth.domain.repository.QuoteRepository

class QuoteRepositoryImpl(
    private val remoteDataSource: QuoteDataSource = RemoteQuoteDataSource()
) : QuoteRepository {

    override suspend fun getQuote(symbol: String): Quote {
        val quoteDto = remoteDataSource.getQuote(symbol)
        return Quote(
            symbol = symbol,
            price = quoteDto.price
        )
    }
}