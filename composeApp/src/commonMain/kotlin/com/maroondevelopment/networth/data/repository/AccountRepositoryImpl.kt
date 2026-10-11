package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.data.datasource.AccountDataSource
import com.maroondevelopment.networth.data.model.getOrNull
import com.maroondevelopment.networth.domain.entity.v2.Account
import com.maroondevelopment.networth.domain.repository.AccountRepository

/**
 * In-memory [AccountRepository] seeded with the single account that backs `investments.json`.
 *
 * Placeholder until accounts get their own persistence — it exists so the portfolio graph
 * can be resolved end to end.
 */
class AccountRepositoryImpl(
    private val dataSource: AccountDataSource
) : AccountRepository {

    private val accounts = mutableListOf<Account>()

    override suspend fun getAccount(accountId: Long): Account? =
        dataSource.getAccount(accountId)?.run { Account(id, name) }

    override suspend fun fetchAccounts(): List<Account> =
        (dataSource.getAllAccounts().getOrNull() ?: emptyList()).map {
            Account(it.id, it.name)
        }

    override suspend fun createAccount(name: String) {
        dataSource.createAccount(name)
    }

    override suspend fun updateAccount(account: Account) {
        val index = accounts.indexOfFirst { it.id == account.id }
        if (index >= 0) accounts[index] = account
    }

    override suspend fun deleteAccount(account: Account) {
        accounts.removeAll { it.id == account.id }
    }
}
