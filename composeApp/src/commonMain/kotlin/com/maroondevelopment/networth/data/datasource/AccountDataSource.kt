package com.maroondevelopment.networth.data.datasource

import com.maroondevelopment.networth.Database
import com.maroondevelopment.networth.data.model.AccountModel
import com.maroondevelopment.networth.data.model.Result

interface AccountDataSource {

    suspend fun getAllAccounts(): Result<List<AccountModel>>
}

class AccountDataSourceImpl(
    private val database: Database
): AccountDataSource {

    override suspend fun getAllAccounts(): Result<List<AccountModel>> {
        val accounts = mutableListOf<AccountModel>()

        try {
            database.accountQueries.selectAll().executeAsList().forEach {
                try {
                    accounts.add(AccountModel(it.id, it.name ?: throw Exception("ERROR: Null account name!")))
                } catch (t: Throwable) {
                    println("[BENG][AccountDataSourceImpl] getAllAccounts() - EXCEPTION!!! ${t.message}")
                }
            }
        } catch (t: Throwable) {
            println("[BENG][AccountDataSourceImpl] getAllAccounts() - EXCEPTION!!! ${t.message}")
        }

        return Result.Success(accounts)
    }
}