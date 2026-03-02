package com.maroondevelopment.networth.domain.repository

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.Quote

interface QuoteRepository {

    suspend fun getQuote(symbol: String, policy: CachePolicy): Quote?

    suspend fun getQuotes(symbols: Set<String>, policy: CachePolicy): Map<String, Quote?>
}