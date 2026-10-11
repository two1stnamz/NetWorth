package com.maroondevelopment.networth.domain.repository

import com.maroondevelopment.networth.domain.entity.v2.Account

interface AccountRepository {

    suspend fun getAccount(accountId: Long): Account?

    suspend fun fetchAccounts(): List<Account>

    suspend fun createAccount(name: String)

    suspend fun updateAccount(account: Account)

    suspend fun deleteAccount(account: Account)
}