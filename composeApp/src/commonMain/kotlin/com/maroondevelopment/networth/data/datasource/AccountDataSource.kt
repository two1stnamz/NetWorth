package com.maroondevelopment.networth.data.datasource

import com.maroondevelopment.networth.Database
import com.maroondevelopment.networth.data.model.AccountModel
import com.maroondevelopment.networth.data.model.Result

interface AccountDataSource {

    suspend fun getAccountsForInstitution(institutionId: Long): Result<List<AccountModel>>

    suspend fun getAccount(id: Long): Result<AccountModel?>

    suspend fun createAccount(institutionId: Long, name: String): Result<Unit>

    suspend fun renameAccount(id: Long, name: String): Result<Unit>

    /** Also removes every position held in the account. */
    suspend fun deleteAccount(id: Long): Result<Unit>
}

class AccountDataSourceImpl(
    private val database: Database
): AccountDataSource {

    override suspend fun getAccountsForInstitution(institutionId: Long): Result<List<AccountModel>> =
        try {
            Result.Success(
                database.accountQueries
                    .selectByInstitution(institutionId)
                    .executeAsList()
                    .map { it.toModel() }
            )
        } catch (t: Throwable) {
            println("[BENG][AccountDataSourceImpl] getAccountsForInstitution() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    override suspend fun getAccount(id: Long): Result<AccountModel?> =
        try {
            Result.Success(
                database.accountQueries
                    .get(id)
                    .executeAsOneOrNull()
                    ?.toModel()
            )
        } catch (t: Throwable) {
            println("[BENG][AccountDataSourceImpl] getAccount() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    override suspend fun createAccount(institutionId: Long, name: String): Result<Unit> =
        try {
            database.accountQueries.insert(institutionId, name)
            Result.Success(Unit)
        } catch (t: Throwable) {
            println("[BENG][AccountDataSourceImpl] createAccount() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    override suspend fun renameAccount(id: Long, name: String): Result<Unit> =
        try {
            database.accountQueries.updateName(name = name, id = id)
            Result.Success(Unit)
        } catch (t: Throwable) {
            println("[BENG][AccountDataSourceImpl] renameAccount() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    /**
     * `Position.account_id` carries no `ON DELETE CASCADE`, so the account's positions are
     * cleared here, in the same transaction as the account itself.
     */
    override suspend fun deleteAccount(id: Long): Result<Unit> =
        try {
            database.transaction {
                database.positionQueries.deleteByAccount(id)
                database.accountQueries.deleteById(id)
            }
            Result.Success(Unit)
        } catch (t: Throwable) {
            println("[BENG][AccountDataSourceImpl] deleteAccount() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    private fun com.maroondevelopment.networth.Account.toModel(): AccountModel =
        AccountModel(id = id, institutionId = institution_id, name = name)
}
