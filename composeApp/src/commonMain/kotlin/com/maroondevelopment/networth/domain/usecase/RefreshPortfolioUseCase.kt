package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.LoadPortfolioOutcome

interface RefreshPortfolioUseCase {

    suspend operator fun invoke(): LoadPortfolioOutcome
}

/**
 * Re-values the portfolio against live quotes and records the resulting total.
 *
 * Recording is tied to the refresh rather than to loading, so the history only ever holds totals
 * taken from live prices — a cache read cannot file a stale value under today.
 *
 * A failed recording does not fail the refresh: the user still gets their updated portfolio.
 */
class RefreshPortfolioUseCaseImpl(
    private val loadPortfolio: LoadPortfolioUseCase,
    private val recordPortfolioValue: RecordPortfolioValueUseCase
) : RefreshPortfolioUseCase {

    override suspend fun invoke(): LoadPortfolioOutcome {
        val outcome = loadPortfolio(CachePolicy.REFRESH)

        if (outcome is LoadPortfolioOutcome.Success) {
            recordPortfolioValue(outcome.portfolio)
        }

        return outcome
    }
}
