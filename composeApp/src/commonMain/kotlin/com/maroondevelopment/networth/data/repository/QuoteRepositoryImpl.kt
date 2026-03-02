package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.data.QuoteDto
import com.maroondevelopment.networth.data.datasource.LocalQuoteDataSource
import com.maroondevelopment.networth.data.datasource.RemoteQuoteDataSource
import com.maroondevelopment.networth.data.datasource.RemoteQuoteDataSourceImpl
import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.Quote
import com.maroondevelopment.networth.domain.repository.QuoteRepository

class QuoteRepositoryImpl(
    private val remoteDataSource: RemoteQuoteDataSource = RemoteQuoteDataSourceImpl(),
    private val localDataSource: LocalQuoteDataSource
) : QuoteRepository {

    override suspend fun getQuote(symbol: String, policy: CachePolicy): Quote? {
        when (policy) {
            CachePolicy.PREFER_CACHE -> {
                val quoteDto = localDataSource.getQuote(symbol)

                return quoteDto?.toEntity()
                    ?: remoteDataSource.getQuote(symbol)?.toEntity()?.also {
                        localDataSource.storeQuote(QuoteDto(symbol, it.price))
                    }
            }
            CachePolicy.REFRESH -> {
                val quoteDto = remoteDataSource.getQuote(symbol)
                val quote = quoteDto?.toEntity()?.also {
                    localDataSource.storeQuote(QuoteDto(symbol, it.price))
                }

                return quote
            }
        }
    }

    private fun QuoteDto.toEntity(): Quote {
        return Quote(
            symbol = this.ticker,
            price = this.price,
        )
    }
}