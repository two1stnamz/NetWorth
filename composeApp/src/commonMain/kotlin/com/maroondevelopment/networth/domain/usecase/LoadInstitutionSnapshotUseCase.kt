package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot
import com.maroondevelopment.networth.domain.entity.v2.InstitutionSnapshot
import com.maroondevelopment.networth.domain.entity.v2.LoadAccountSnapshotOutcome
import com.maroondevelopment.networth.domain.entity.v2.LoadInstitutionSnapshotOutcome
import com.maroondevelopment.networth.domain.repository.AccountRepository
import com.maroondevelopment.networth.domain.repository.InstitutionRepository

interface LoadInstitutionSnapshotUseCase {

    suspend operator fun invoke(
        institutionId: Long,
        policy: CachePolicy
    ): LoadInstitutionSnapshotOutcome
}

/**
 * Values every account held at one institution.
 *
 * An account whose snapshot fails to load is reported as a failure for the whole institution
 * rather than quietly dropped, so a total is never shown short of one of its accounts.
 */
class LoadInstitutionSnapshotUseCaseImpl(
    private val institutionRepository: InstitutionRepository,
    private val accountRepository: AccountRepository,
    private val loadAccountSnapshot: LoadAccountSnapshotUseCase
) : LoadInstitutionSnapshotUseCase {

    override suspend fun invoke(
        institutionId: Long,
        policy: CachePolicy
    ): LoadInstitutionSnapshotOutcome =
        try {
            val institution = institutionRepository.getInstitution(institutionId)

            if (institution == null) {
                LoadInstitutionSnapshotOutcome.Failure.InstitutionNotFound
            } else {
                val accounts = accountRepository.getAccountsForInstitution(institution.id)
                val snapshots = mutableListOf<AccountSnapshot>()

                for (account in accounts) {
                    when (val outcome = loadAccountSnapshot(account.id, policy)) {
                        is LoadAccountSnapshotOutcome.Success -> snapshots.add(outcome.snapshot)
                        LoadAccountSnapshotOutcome.Failure.AccountNotFound ->
                            return LoadInstitutionSnapshotOutcome.Failure.Technical(
                                "Account ${account.id} disappeared while loading"
                            )
                        is LoadAccountSnapshotOutcome.Failure.Technical ->
                            return LoadInstitutionSnapshotOutcome.Failure.Technical(outcome.error)
                    }
                }

                LoadInstitutionSnapshotOutcome.Success(
                    InstitutionSnapshot(institution, snapshots)
                )
            }
        } catch (t: Throwable) {
            LoadInstitutionSnapshotOutcome.Failure.Technical(t.message ?: "Unknown error")
        }
}
