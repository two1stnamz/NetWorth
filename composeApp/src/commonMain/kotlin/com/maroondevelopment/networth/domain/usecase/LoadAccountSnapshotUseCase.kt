package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.Quote
import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot
import com.maroondevelopment.networth.domain.entity.v2.Asset
import com.maroondevelopment.networth.domain.entity.v2.LoadAccountSnapshotOutcome
import com.maroondevelopment.networth.domain.repository.AccountRepository
import com.maroondevelopment.networth.domain.repository.PositionRepository
import com.maroondevelopment.networth.domain.repository.QuoteRepository

interface LoadAccountSnapshotUseCase {

    suspend operator fun invoke(accountId: Long, policy: CachePolicy): LoadAccountSnapshotOutcome
}

/**
 * Pairs every position in an account with its latest quote.
 *
 * A position whose quote cannot be resolved is still reported, valued at zero, so a quote
 * outage never makes a holding disappear from the account the user is editing.
 */
class LoadAccountSnapshotUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val positionRepository: PositionRepository,
    private val quoteRepository: QuoteRepository
) : LoadAccountSnapshotUseCase {

    override suspend fun invoke(
        accountId: Long,
        policy: CachePolicy
    ): LoadAccountSnapshotOutcome =
        try {
            val account = accountRepository.getAccount(accountId)

            if (account == null) {
                LoadAccountSnapshotOutcome.Failure.AccountNotFound
            } else {
                val positions = positionRepository.getPositionsForAccount(account.id)
                val tickers = positions.mapTo(mutableSetOf()) { it.ticker }
                val quotes =
                    if (tickers.isEmpty()) emptyMap()
                    else quoteRepository.getQuotes(tickers, policy)

                val assets = positions.map { position ->
                    Asset(
                        position = position,
                        quote = quotes[position.ticker] ?: Quote(position.ticker, 0.0, 0.0)
                    )
                }

                LoadAccountSnapshotOutcome.Success(AccountSnapshot(account, assets))
            }
        } catch (t: Throwable) {
            LoadAccountSnapshotOutcome.Failure.Technical(t.message ?: "Unknown error")
        }
}
