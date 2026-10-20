package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.AddPositionOutcome
import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.v2.LoadAccountSnapshotOutcome
import com.maroondevelopment.networth.domain.entity.v2.NewPosition
import com.maroondevelopment.networth.domain.repository.PositionRepository
import com.maroondevelopment.networth.domain.repository.QuoteRepository

interface AddPositionToAccountUseCase {

    suspend operator fun invoke(accountId: Long, position: NewPosition): AddPositionOutcome
}

/**
 * Adds a position to an account, rejecting input the account cannot hold: blank tickers,
 * non-positive units, a ticker the account already holds, and tickers with no tradeable quote.
 *
 * On success the freshly loaded snapshot comes back, so the caller never has to re-derive state.
 */
class AddPositionToAccountUseCaseImpl(
    private val positionRepository: PositionRepository,
    private val quoteRepository: QuoteRepository,
    private val loadAccountSnapshot: LoadAccountSnapshotUseCase
) : AddPositionToAccountUseCase {

    override suspend fun invoke(accountId: Long, position: NewPosition): AddPositionOutcome {
        val ticker = position.ticker.trim().uppercase()

        if (ticker.isEmpty() || position.units <= 0.0) {
            return AddPositionOutcome.Failure.InvalidInput
        }

        return try {
            val held = positionRepository.getPositionsForAccount(accountId)

            if (held.any { it.ticker.equals(ticker, ignoreCase = true) }) {
                return AddPositionOutcome.Failure.DuplicateTicker
            }

            // A ticker that resolves to no price is not something the account can be valued on.
            val quote = quoteRepository.getQuote(ticker, CachePolicy.REFRESH)

            if (quote == null || quote.price <= 0.0) {
                return AddPositionOutcome.Failure.NoQuote
            }

            positionRepository.addPositionsForAccount(
                accountId = accountId,
                positions = listOf(NewPosition(ticker = ticker, units = position.units))
            )

            when (val outcome = loadAccountSnapshot(accountId, CachePolicy.PREFER_CACHE)) {
                is LoadAccountSnapshotOutcome.Success -> AddPositionOutcome.Success(outcome.snapshot)
                LoadAccountSnapshotOutcome.Failure.AccountNotFound ->
                    AddPositionOutcome.Failure.Technical("Account $accountId no longer exists")
                is LoadAccountSnapshotOutcome.Failure.Technical ->
                    AddPositionOutcome.Failure.Technical(outcome.error)
            }
        } catch (t: Throwable) {
            AddPositionOutcome.Failure.Technical(t.message ?: "Unknown error")
        }
    }
}
