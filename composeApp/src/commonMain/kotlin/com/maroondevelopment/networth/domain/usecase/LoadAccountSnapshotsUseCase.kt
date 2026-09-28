package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.LoadPortfolioOutcome
import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot
import com.maroondevelopment.networth.domain.entity.v2.Asset
import com.maroondevelopment.networth.domain.repository.AccountRepository
import com.maroondevelopment.networth.domain.repository.PositionRepository
import com.maroondevelopment.networth.domain.repository.QuoteRepository

interface LoadAccountSnapshotsUseCase {

    suspend operator fun invoke(policy: CachePolicy): LoadPortfolioOutcome
}

class LoadAccountSnapshotsUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val positionRepository: PositionRepository,
    private val quoteRepository: QuoteRepository
): LoadAccountSnapshotsUseCase {

    override suspend fun invoke(policy: CachePolicy): LoadPortfolioOutcome {
        val accounts = accountRepository.fetchAccounts()
        val snapshots = mutableListOf<AccountSnapshot>()

        accounts.forEach { it ->
            val positions = positionRepository.getPositionsForAccount(it.id)
            val tickers = positions.mapTo(mutableSetOf()) { it.ticker }
            val quotes = quoteRepository.getQuotes(tickers, CachePolicy.PREFER_CACHE)
            val assets = mutableListOf<Asset>()

            positions.forEach { position ->
                quotes[position.ticker]?.let {
                    assets.add(Asset(position, it))
                }
            }

            snapshots.add(AccountSnapshot(it, assets))
        }

        return LoadPortfolioOutcome.Success(snapshots)
    }
}