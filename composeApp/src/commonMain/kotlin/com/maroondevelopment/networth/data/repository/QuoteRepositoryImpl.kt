package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.data.QuoteDto
import com.maroondevelopment.networth.data.datasource.LocalQuoteDataSource
import com.maroondevelopment.networth.data.datasource.RemoteQuoteDataSource
import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.Quote
import com.maroondevelopment.networth.domain.repository.QuoteRepository

class QuoteRepositoryImpl(
    private val remoteDataSource: RemoteQuoteDataSource,
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

    override suspend fun getQuotes(
        symbols: Set<String>,
        policy: CachePolicy
    ): Map<String, Quote?> {
        return when (policy) {
            CachePolicy.PREFER_CACHE -> {
                val result = mutableMapOf<String, Quote?>()
                val cacheMisses = mutableSetOf<String>()

                for (symbol in symbols) {
                    val cached = localDataSource.getQuote(symbol)
                    if (cached != null) {
                        result[symbol] = cached.toEntity()
                    } else {
                        cacheMisses.add(symbol)
                    }
                }

                if (cacheMisses.isNotEmpty()) {
                    remoteDataSource.getQuotes(cacheMisses).forEach { (symbol, dto) ->
                        result[symbol] = dto?.toEntity()?.also {
                            localDataSource.storeQuote(QuoteDto(symbol, it.price))
                        }
                    }
                }

                result
            }
            CachePolicy.REFRESH -> {
                val result = mutableMapOf<String, Quote?>()
                remoteDataSource.getQuotes(symbols).forEach { (symbol, dto) ->
                    result[symbol] = dto?.toEntity()?.also {
                        localDataSource.storeQuote(QuoteDto(symbol, it.price))
                    }
                }
                result
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