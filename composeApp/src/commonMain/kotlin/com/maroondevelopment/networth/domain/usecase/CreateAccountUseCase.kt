package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.v2.CreateAccountOutcome
import com.maroondevelopment.networth.domain.repository.AccountRepository

interface CreateAccountUseCase {

    suspend operator fun invoke(name: String): CreateAccountOutcome
}

class CreateAccountUseCaseImpl(
    private val accountRepository: AccountRepository
) : CreateAccountUseCase {

    override suspend fun invoke(name: String): CreateAccountOutcome {
        accountRepository.createAccount(name)
        return CreateAccountOutcome.Success
    }
}