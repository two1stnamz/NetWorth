package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.LoadPortfolioOutcome
import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot
import com.maroondevelopment.networth.domain.entity.v2.LoadAccountSnapshotOutcome
import com.maroondevelopment.networth.domain.entity.v2.Portfolio
import com.maroondevelopment.networth.domain.repository.AccountRepository

interface LoadPortfolioUseCase {

    suspend operator fun invoke(policy: CachePolicy): LoadPortfolioOutcome
}

/**
 * Loads every account, values each one's positions against their quotes, and returns them as a
 * single [Portfolio] whose totals span the whole holding.
 */
class LoadPortfolioUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val loadAccountSnapshot: LoadAccountSnapshotUseCase
): LoadPortfolioUseCase {

    override suspend fun invoke(policy: CachePolicy): LoadPortfolioOutcome {
        val accounts = accountRepository.fetchAccounts()
        val snapshots = mutableListOf<AccountSnapshot>()

        accounts.forEach { account ->
            when (val outcome = loadAccountSnapshot(account.id, policy)) {
                is LoadAccountSnapshotOutcome.Success -> snapshots.add(outcome.snapshot)
                is LoadAccountSnapshotOutcome.Failure -> return LoadPortfolioOutcome.Error
            }
        }

        return LoadPortfolioOutcome.Success(Portfolio(snapshots))
    }
}
