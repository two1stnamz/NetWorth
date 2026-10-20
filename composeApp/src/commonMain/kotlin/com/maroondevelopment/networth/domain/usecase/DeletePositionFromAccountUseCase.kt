package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.v2.DeletePositionOutcome
import com.maroondevelopment.networth.domain.entity.v2.LoadAccountSnapshotOutcome
import com.maroondevelopment.networth.domain.repository.PositionRepository

interface DeletePositionFromAccountUseCase {

    suspend operator fun invoke(accountId: Long, ticker: String): DeletePositionOutcome
}

class DeletePositionFromAccountUseCaseImpl(
    private val positionRepository: PositionRepository,
    private val loadAccountSnapshot: LoadAccountSnapshotUseCase
) : DeletePositionFromAccountUseCase {

    override suspend fun invoke(accountId: Long, ticker: String): DeletePositionOutcome =
        try {
            val held = positionRepository.getPositionsForAccount(accountId)
                .firstOrNull { it.ticker.equals(ticker, ignoreCase = true) }

            if (held == null) {
                DeletePositionOutcome.Failure.NotFound
            } else {
                positionRepository.deletePositionForAccount(accountId, held.ticker)

                when (val outcome = loadAccountSnapshot(accountId, CachePolicy.PREFER_CACHE)) {
                    is LoadAccountSnapshotOutcome.Success ->
                        DeletePositionOutcome.Success(outcome.snapshot)
                    LoadAccountSnapshotOutcome.Failure.AccountNotFound ->
                        DeletePositionOutcome.Failure.Technical("Account $accountId no longer exists")
                    is LoadAccountSnapshotOutcome.Failure.Technical ->
                        DeletePositionOutcome.Failure.Technical(outcome.error)
                }
            }
        } catch (t: Throwable) {
            DeletePositionOutcome.Failure.Technical(t.message ?: "Unknown error")
        }
}
