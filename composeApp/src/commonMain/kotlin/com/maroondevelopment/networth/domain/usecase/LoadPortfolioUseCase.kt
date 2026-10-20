package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.LoadPortfolioOutcome
import com.maroondevelopment.networth.domain.entity.v2.InstitutionSnapshot
import com.maroondevelopment.networth.domain.entity.v2.LoadInstitutionSnapshotOutcome
import com.maroondevelopment.networth.domain.entity.v2.Portfolio
import com.maroondevelopment.networth.domain.repository.InstitutionRepository

interface LoadPortfolioUseCase {

    suspend operator fun invoke(policy: CachePolicy): LoadPortfolioOutcome
}

/**
 * Loads every institution, values the accounts held at each, and returns them as a single
 * [Portfolio] whose totals span the whole holding.
 */
class LoadPortfolioUseCaseImpl(
    private val institutionRepository: InstitutionRepository,
    private val loadInstitutionSnapshot: LoadInstitutionSnapshotUseCase
): LoadPortfolioUseCase {

    override suspend fun invoke(policy: CachePolicy): LoadPortfolioOutcome {
        val institutions = institutionRepository.fetchInstitutions()
        val snapshots = mutableListOf<InstitutionSnapshot>()

        institutions.forEach { institution ->
            when (val outcome = loadInstitutionSnapshot(institution.id, policy)) {
                is LoadInstitutionSnapshotOutcome.Success -> snapshots.add(outcome.snapshot)
                is LoadInstitutionSnapshotOutcome.Failure -> return LoadPortfolioOutcome.Error
            }
        }

        return LoadPortfolioOutcome.Success(Portfolio(snapshots))
    }
}
