package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot
import com.maroondevelopment.networth.domain.entity.v2.Asset
import com.maroondevelopment.networth.domain.repository.AccountRepository
import com.maroondevelopment.networth.domain.repository.PositionRepository
import com.maroondevelopment.networth.domain.repository.QuoteRepository

interface LoadAccountSnapshotUseCase {

    suspend operator fun invoke(accountId: String, policy: CachePolicy): AccountSnapshot
}

class LoadAccountSnapshotUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val positionRepository: PositionRepository,
    private val quoteRepository: QuoteRepository
) : LoadAccountSnapshotUseCase {

    override suspend fun invoke(accountId: String, policy: CachePolicy): AccountSnapshot {
        val account = accountRepository.getAccount(accountId)
        val positions = positionRepository.getPositionsForAccount(account.id)
        val tickers = positions.mapTo(mutableSetOf()) { it.ticker }
        val quotes = quoteRepository.getQuotes(tickers, policy)
        val assets = mutableListOf<Asset>()

        positions.forEach { position ->
            quotes[position.ticker]?.let {
                assets.add(Asset(position, it))
            }
        }

        return AccountSnapshot(account, assets)
    }
}