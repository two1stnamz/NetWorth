package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.LoadPortfolioOutcome
import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot
import com.maroondevelopment.networth.domain.entity.v2.Asset
import com.maroondevelopment.networth.domain.repository.AccountRepository
import com.maroondevelopment.networth.domain.repository.PositionRepository
import com.maroondevelopment.networth.domain.repository.QuoteRepository

interface LoadPortfolioUseCase {

    suspend operator fun invoke(policy: CachePolicy): LoadPortfolioOutcome
}

class LoadPortfolioUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val loadAccountSnapshot: LoadAccountSnapshotUseCase
): LoadPortfolioUseCase {

    override suspend fun invoke(policy: CachePolicy): LoadPortfolioOutcome {
        val accounts = accountRepository.fetchAccounts()
        val snapshots = mutableListOf<AccountSnapshot>()

        accounts.forEach {
            snapshots.add(loadAccountSnapshot(it.id, policy))
        }

        return LoadPortfolioOutcome.Success(snapshots)
    }
}