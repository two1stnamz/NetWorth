package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.LoadPortfolioOutcome
import com.maroondevelopment.networth.domain.entity.LoadStrategy
import com.maroondevelopment.networth.domain.repository.AccountRepository
import com.maroondevelopment.networth.domain.repository.PositionRepository
import com.maroondevelopment.networth.domain.repository.QuoteRepository

interface LoadPortfolioUseCase {

    suspend operator fun invoke(strategy: LoadStrategy): LoadPortfolioOutcome
}

class LoadPortfolioUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val positionRepository: PositionRepository,
    private val quoteRepository: QuoteRepository
): LoadPortfolioUseCase {

    override suspend fun invoke(strategy: LoadStrategy): LoadPortfolioOutcome {
        val accounts = accountRepository.fetchAccounts()
        val tickers = mutableSetOf<String>()

        accounts.forEach {
            it.positions.forEach { position ->
                tickers.add(position.ticker)
            }
        }

        val quotes = quoteRepository.getQuotes(tickers, CachePolicy.PREFER_CACHE)

    }
}