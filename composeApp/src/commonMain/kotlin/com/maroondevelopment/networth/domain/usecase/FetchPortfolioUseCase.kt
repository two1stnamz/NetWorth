package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.Asset
import com.maroondevelopment.networth.domain.entity.Portfolio
import com.maroondevelopment.networth.domain.repository.HoldingsRepository
import com.maroondevelopment.networth.domain.repository.QuoteRepository

class FetchPortfolioUseCase(
    private val holdingsRepository: HoldingsRepository,
    private val quoteRepository: QuoteRepository
) {

    suspend operator fun invoke(): Portfolio {
        val holdings = holdingsRepository.getHoldings()
        val assets = mutableListOf<Asset>()

        for (holding in holdings) {
            quoteRepository.getQuote(holding.symbol)?.let { quote ->

                val asset = Asset(
                    holding,
                    value = quote.price * holding.quantity
                )
                assets.add(asset)
            }
        }

        return Portfolio(
            assets = assets,
            totalValue = assets.sumOf { it.value }
        )
    }
}