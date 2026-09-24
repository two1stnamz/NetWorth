package com.maroondevelopment.networth.domain.repository

import com.maroondevelopment.networth.domain.entity.Account

interface AccountRepository {

    suspend fun fetchAccounts(): List<Account>

    suspend fun createAccount(name: String): Account

    suspend fun updateAccount(account: Account)

    suspend fun deleteAccount(account: Account)
}