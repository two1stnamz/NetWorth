package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.LoadPortfolioOutcome
import com.maroondevelopment.networth.domain.entity.v2.RenameInstitutionOutcome
import com.maroondevelopment.networth.domain.repository.InstitutionRepository

interface RenameInstitutionUseCase {

    suspend operator fun invoke(institutionId: Long, name: String): RenameInstitutionOutcome
}

/**
 * Changes an institution's name.
 *
 * The institution has to exist first, so a stale card cannot resurrect one someone else removed,
 * and the portfolio is re-read afterwards so the caller never patches its own list.
 */
class RenameInstitutionUseCaseImpl(
    private val institutionRepository: InstitutionRepository,
    private val loadPortfolio: LoadPortfolioUseCase
) : RenameInstitutionUseCase {

    override suspend fun invoke(institutionId: Long, name: String): RenameInstitutionOutcome {
        val trimmed = name.trim()

        if (trimmed.isEmpty()) return RenameInstitutionOutcome.Failure.InvalidInput

        return try {
            institutionRepository.getInstitution(institutionId)
                ?: return RenameInstitutionOutcome.Failure.NotFound

            institutionRepository.renameInstitution(institutionId, trimmed)

            when (val outcome = loadPortfolio(CachePolicy.PREFER_CACHE)) {
                is LoadPortfolioOutcome.Success -> RenameInstitutionOutcome.Success(outcome.portfolio)
                LoadPortfolioOutcome.Error ->
                    RenameInstitutionOutcome.Failure.Technical("Could not re-read the portfolio")
            }
        } catch (t: Throwable) {
            RenameInstitutionOutcome.Failure.Technical(t.message ?: "Unknown error")
        }
    }
}
