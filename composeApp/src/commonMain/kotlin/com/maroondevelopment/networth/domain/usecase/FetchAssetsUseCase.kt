package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.Asset
import com.maroondevelopment.networth.domain.repository.HoldingsRepository
import com.maroondevelopment.networth.domain.repository.QuoteRepository

class FetchAssetsUseCase(
    private val holdingsRepository: HoldingsRepository,
    private val quoteRepository: QuoteRepository
) {

    suspend operator fun invoke(): List<Asset> {
        val holdings = holdingsRepository.getHoldings()
        val assets = mutableListOf<Asset>()

        for (holding in holdings) {
            val quote = quoteRepository.getQuote(holding.symbol)
            val asset = Asset(
                holding,
                quote
            )
            assets.add(asset)
        }

        return assets
    }
}