package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.CachePolicy
import com.maroondevelopment.networth.domain.entity.v2.LoadInstitutionSnapshotOutcome
import com.maroondevelopment.networth.domain.entity.v2.RenameAccountOutcome
import com.maroondevelopment.networth.domain.repository.AccountRepository

interface RenameAccountUseCase {

    suspend operator fun invoke(accountId: Long, name: String): RenameAccountOutcome
}

/**
 * Changes an account's name.
 *
 * The account is read first both to reject a stale card and to learn which institution to
 * re-read, so the caller gets back a snapshot it can show as-is.
 */
class RenameAccountUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val loadInstitutionSnapshot: LoadInstitutionSnapshotUseCase
) : RenameAccountUseCase {

    override suspend fun invoke(accountId: Long, name: String): RenameAccountOutcome {
        val trimmed = name.trim()

        if (trimmed.isEmpty()) return RenameAccountOutcome.Failure.InvalidInput

        return try {
            val account = accountRepository.getAccount(accountId)
                ?: return RenameAccountOutcome.Failure.NotFound

            accountRepository.renameAccount(accountId, trimmed)

            when (
                val outcome =
                    loadInstitutionSnapshot(account.institutionId, CachePolicy.PREFER_CACHE)
            ) {
                is LoadInstitutionSnapshotOutcome.Success ->
                    RenameAccountOutcome.Success(outcome.snapshot)
                LoadInstitutionSnapshotOutcome.Failure.InstitutionNotFound ->
                    RenameAccountOutcome.Failure.Technical(
                        "Institution ${account.institutionId} no longer exists"
                    )
                is LoadInstitutionSnapshotOutcome.Failure.Technical ->
                    RenameAccountOutcome.Failure.Technical(outcome.error)
            }
        } catch (t: Throwable) {
            RenameAccountOutcome.Failure.Technical(t.message ?: "Unknown error")
        }
    }
}
