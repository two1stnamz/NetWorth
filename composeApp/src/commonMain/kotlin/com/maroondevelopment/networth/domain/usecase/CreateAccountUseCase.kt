package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.v2.CreateAccountOutcome
import com.maroondevelopment.networth.domain.repository.AccountRepository

interface CreateAccountUseCase {

    /** Accounts always belong to an institution, so [institutionId] is required. */
    suspend operator fun invoke(institutionId: Long, name: String): CreateAccountOutcome
}

class CreateAccountUseCaseImpl(
    private val accountRepository: AccountRepository
) : CreateAccountUseCase {

    override suspend fun invoke(institutionId: Long, name: String): CreateAccountOutcome {
        val trimmed = name.trim()

        if (trimmed.isEmpty()) return CreateAccountOutcome.Error

        return try {
            accountRepository.createAccount(institutionId, trimmed)
            CreateAccountOutcome.Success
        } catch (t: Throwable) {
            println("[BENG][CreateAccountUseCase] invoke() - EXCEPTION!!! ${t.message}")
            CreateAccountOutcome.Error
        }
    }
}
