package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.domain.entity.v2.Account
import com.maroondevelopment.networth.domain.repository.AccountRepository

/**
 * In-memory [AccountRepository] seeded with the single account that backs `investments.json`.
 *
 * Placeholder until accounts get their own persistence — it exists so the portfolio graph
 * can be resolved end to end.
 */
class AccountRepositoryImpl : AccountRepository {

    private val accounts = mutableListOf<Account>()

    override suspend fun getAccount(accountId: String): Account =
        accounts.first { it.id == accountId }

    override suspend fun fetchAccounts(): List<Account> = accounts.toList()

    override suspend fun createAccount(name: String): Account =
        Account(id = "account-${accounts.size + 1}", name = name).also { accounts.add(it) }

    override suspend fun updateAccount(account: Account) {
        val index = accounts.indexOfFirst { it.id == account.id }
        if (index >= 0) accounts[index] = account
    }

    override suspend fun deleteAccount(account: Account) {
        accounts.removeAll { it.id == account.id }
    }
}
