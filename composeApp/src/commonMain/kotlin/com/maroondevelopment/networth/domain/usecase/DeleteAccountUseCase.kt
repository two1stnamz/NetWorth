package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.v2.DeleteAccountOutcome
import com.maroondevelopment.networth.domain.entity.v2.LoadInstitutionSnapshotOutcome
import com.maroondevelopment.networth.domain.repository.AccountRepository

interface DeleteAccountUseCase {

    suspend operator fun invoke(accountId: Long): DeleteAccountOutcome
}

/**
 * Removes an account along with its positions.
 *
 * The cascade itself belongs to the data layer, which clears the positions in the same
 * transaction; the account is read first so its institution is known before it is gone.
 */
class DeleteAccountUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val loadInstitutionSnapshot: LoadInstitutionSnapshotUseCase
) : DeleteAccountUseCase {

    override suspend fun invoke(accountId: Long): DeleteAccountOutcome =
        try {
            val account = accountRepository.getAccount(accountId)

            if (account == null) {
                DeleteAccountOutcome.Failure.NotFound
            } else {
                accountRepository.deleteAccount(accountId)

                when (
                    val outcome =
                        loadInstitutionSnapshot(account.institutionId, CachePolicy.PREFER_CACHE)
                ) {
                    is LoadInstitutionSnapshotOutcome.Success ->
                        DeleteAccountOutcome.Success(outcome.snapshot)
                    LoadInstitutionSnapshotOutcome.Failure.InstitutionNotFound ->
                        DeleteAccountOutcome.Failure.Technical(
                            "Institution ${account.institutionId} no longer exists"
                        )
                    is LoadInstitutionSnapshotOutcome.Failure.Technical ->
                        DeleteAccountOutcome.Failure.Technical(outcome.error)
                }
            }
        } catch (t: Throwable) {
            DeleteAccountOutcome.Failure.Technical(t.message ?: "Unknown error")
        }
}
