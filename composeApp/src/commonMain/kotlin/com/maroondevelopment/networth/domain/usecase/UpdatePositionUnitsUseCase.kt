package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.v2.LoadAccountSnapshotOutcome
import com.maroondevelopment.networth.domain.entity.v2.NewPosition
import com.maroondevelopment.networth.domain.entity.v2.UpdatePositionOutcome
import com.maroondevelopment.networth.domain.repository.PositionRepository

interface UpdatePositionUnitsUseCase {

    suspend operator fun invoke(
        accountId: Long,
        ticker: String,
        units: Double
    ): UpdatePositionOutcome
}

/**
 * Changes how many units of a ticker an account holds.
 *
 * Units must be positive — zeroing out a holding is a delete, not an edit — and the account must
 * already hold the ticker, so a stale card cannot resurrect a position someone else removed.
 */
class UpdatePositionUnitsUseCaseImpl(
    private val positionRepository: PositionRepository,
    private val loadAccountSnapshot: LoadAccountSnapshotUseCase
) : UpdatePositionUnitsUseCase {

    override suspend fun invoke(
        accountId: Long,
        ticker: String,
        units: Double
    ): UpdatePositionOutcome {
        if (units <= 0.0) return UpdatePositionOutcome.Failure.InvalidInput

        return try {
            val held = positionRepository.getPositionsForAccount(accountId)
                .firstOrNull { it.ticker.equals(ticker, ignoreCase = true) }
                ?: return UpdatePositionOutcome.Failure.NotFound

            positionRepository.updatePositionForAccount(
                accountId = accountId,
                position = NewPosition(ticker = held.ticker, units = units)
            )

            when (val outcome = loadAccountSnapshot(accountId, CachePolicy.PREFER_CACHE)) {
                is LoadAccountSnapshotOutcome.Success ->
                    UpdatePositionOutcome.Success(outcome.snapshot)
                LoadAccountSnapshotOutcome.Failure.AccountNotFound ->
                    UpdatePositionOutcome.Failure.Technical("Account $accountId no longer exists")
                is LoadAccountSnapshotOutcome.Failure.Technical ->
                    UpdatePositionOutcome.Failure.Technical(outcome.error)
            }
        } catch (t: Throwable) {
            UpdatePositionOutcome.Failure.Technical(t.message ?: "Unknown error")
        }
    }
}
