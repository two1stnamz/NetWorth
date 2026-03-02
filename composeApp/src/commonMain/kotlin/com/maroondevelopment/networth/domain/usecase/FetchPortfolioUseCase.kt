package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.Asset
import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.Portfolio
import com.maroondevelopment.networth.domain.repository.HoldingsRepository
import com.maroondevelopment.networth.domain.repository.QuoteRepository
import com.maroondevelopment.networth.util.roundTo2Decimals

class FetchPortfolioUseCase(
    private val holdingsRepository: HoldingsRepository,
    private val quoteRepository: QuoteRepository
) {

    suspend operator fun invoke(cachePolicy: CachePolicy): Portfolio {
        val holdings = holdingsRepository.getHoldings()
        val symbols = holdings.map { it.symbol }.toSet()
        val quotes = quoteRepository.getQuotes(symbols, cachePolicy)
        val assets = mutableListOf<Asset>()

        for (holding in holdings) {
            val price = quotes[holding.symbol]?.price ?: 0.0
            val asset = Asset(
                holding,
                value = (price * holding.quantity).roundTo2Decimals()
            )
            assets.add(asset)
        }

        return Portfolio(
            assets = assets,
            totalValue = assets.sumOf { it.value }.roundTo2Decimals()
        )
    }
}