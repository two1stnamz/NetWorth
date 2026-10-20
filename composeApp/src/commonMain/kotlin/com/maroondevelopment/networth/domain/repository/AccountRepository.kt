package com.maroondevelopment.networth.domain.repository

import com.maroondevelopment.networth.domain.entity.v2.Account

interface AccountRepository {

    suspend fun getAccount(accountId: Long): Account?

    suspend fun getAccountsForInstitution(institutionId: Long): List<Account>

    suspend fun createAccount(institutionId: Long, name: String)

    suspend fun renameAccount(accountId: Long, name: String)

    /** Also removes every position held in the account. */
    suspend fun deleteAccount(accountId: Long)
}
