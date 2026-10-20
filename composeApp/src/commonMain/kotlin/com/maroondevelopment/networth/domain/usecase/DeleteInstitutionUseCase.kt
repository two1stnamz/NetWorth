package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.LoadPortfolioOutcome
import com.maroondevelopment.networth.domain.entity.v2.DeleteInstitutionOutcome
import com.maroondevelopment.networth.domain.repository.InstitutionRepository

interface DeleteInstitutionUseCase {

    suspend operator fun invoke(institutionId: Long): DeleteInstitutionOutcome
}

/**
 * Removes an institution along with its accounts and their positions.
 *
 * The cascade itself belongs to the data layer, which clears the children in one transaction;
 * this use case only guards the institution's existence and re-reads the portfolio afterwards.
 */
class DeleteInstitutionUseCaseImpl(
    private val institutionRepository: InstitutionRepository,
    private val loadPortfolio: LoadPortfolioUseCase
) : DeleteInstitutionUseCase {

    override suspend fun invoke(institutionId: Long): DeleteInstitutionOutcome =
        try {
            if (institutionRepository.getInstitution(institutionId) == null) {
                DeleteInstitutionOutcome.Failure.NotFound
            } else {
                institutionRepository.deleteInstitution(institutionId)

                when (val outcome = loadPortfolio(CachePolicy.PREFER_CACHE)) {
                    is LoadPortfolioOutcome.Success ->
                        DeleteInstitutionOutcome.Success(outcome.portfolio)
                    LoadPortfolioOutcome.Error ->
                        DeleteInstitutionOutcome.Failure.Technical("Could not re-read the portfolio")
                }
            }
        } catch (t: Throwable) {
            DeleteInstitutionOutcome.Failure.Technical(t.message ?: "Unknown error")
        }
}
