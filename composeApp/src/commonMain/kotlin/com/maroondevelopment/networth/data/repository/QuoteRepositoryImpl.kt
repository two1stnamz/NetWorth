package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.data.QuoteDto
import com.maroondevelopment.networth.data.datasource.LocalQuoteDataSource
import com.maroondevelopment.networth.data.datasource.RemoteQuoteDataSource
import com.maroondevelopment.networth.data.datasource.RemoteQuoteDataSourceImpl
import com.maroondevelopment.networth.domain.entity.Quote
import com.maroondevelopment.networth.domain.repository.QuoteRepository

class QuoteRepositoryImpl(
    private val remoteDataSource: RemoteQuoteDataSource = RemoteQuoteDataSourceImpl(),
    private val localDataSource: LocalQuoteDataSource
) : QuoteRepository {

    override suspend fun getQuote(symbol: String): Quote? {
        val quoteDto = localDataSource.getQuote(symbol)

        if (quoteDto != null) {
            return quoteDto.toEntity()
        }

        val remoteQuoteDto = remoteDataSource.getQuote(symbol)

        if (remoteQuoteDto != null) {
            // Optionally, you could save this to the local data source for caching
             localDataSource.storeQuote(remoteQuoteDto)
            return remoteQuoteDto.toEntity()
        }

        return null
    }

    private fun QuoteDto.toEntity(): Quote {
        return Quote(
            symbol = this.ticker,
            price = this.price,
        )
    }
}