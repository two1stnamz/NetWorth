package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.v2.Account
import com.maroondevelopment.networth.domain.repository.AccountRepository

interface CreateAccountUseCase {

    suspend operator fun invoke(name: String): Account
}

class CreateAccountUseCaseImpl(
    private val accountRepository: AccountRepository
) : CreateAccountUseCase {

    override suspend fun invoke(name: String): Account {
        return accountRepository.createAccount(name)
    }
}