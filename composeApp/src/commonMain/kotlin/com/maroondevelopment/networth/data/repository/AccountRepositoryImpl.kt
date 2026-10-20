package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.data.datasource.AccountDataSource
import com.maroondevelopment.networth.data.model.AccountModel
import com.maroondevelopment.networth.data.model.getOrNull
import com.maroondevelopment.networth.data.model.getOrThrow
import com.maroondevelopment.networth.domain.entity.v2.Account
import com.maroondevelopment.networth.domain.repository.AccountRepository

/**
 * [AccountRepository] backed by the `Account` table, scoped by `institution_id`.
 *
 * Reads degrade to nothing so a storage failure cannot take a screen down, while writes throw
 * so the calling use case can report the failure instead of silently claiming success.
 */
class AccountRepositoryImpl(
    private val dataSource: AccountDataSource
) : AccountRepository {

    override suspend fun getAccount(accountId: Long): Account? =
        dataSource.getAccount(accountId).getOrNull()?.toEntity()

    override suspend fun getAccountsForInstitution(institutionId: Long): List<Account> =
        dataSource.getAccountsForInstitution(institutionId).getOrNull()?.map { it.toEntity() }
            ?: emptyList()

    override suspend fun createAccount(institutionId: Long, name: String) {
        dataSource.createAccount(institutionId, name).getOrThrow()
    }

    override suspend fun renameAccount(accountId: Long, name: String) {
        dataSource.renameAccount(accountId, name).getOrThrow()
    }

    override suspend fun deleteAccount(accountId: Long) {
        dataSource.deleteAccount(accountId).getOrThrow()
    }

    private fun AccountModel.toEntity(): Account =
        Account(id = id, institutionId = institutionId, name = name)
}
