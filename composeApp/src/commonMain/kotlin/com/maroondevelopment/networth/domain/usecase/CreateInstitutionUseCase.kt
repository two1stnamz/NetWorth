package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.v2.CreateInstitutionOutcome
import com.maroondevelopment.networth.domain.repository.InstitutionRepository

interface CreateInstitutionUseCase {

    suspend operator fun invoke(name: String): CreateInstitutionOutcome
}

class CreateInstitutionUseCaseImpl(
    private val institutionRepository: InstitutionRepository
) : CreateInstitutionUseCase {

    override suspend fun invoke(name: String): CreateInstitutionOutcome {
        val trimmed = name.trim()

        if (trimmed.isEmpty()) return CreateInstitutionOutcome.Error

        return try {
            institutionRepository.createInstitution(trimmed)
            CreateInstitutionOutcome.Success
        } catch (t: Throwable) {
            println("[BENG][CreateInstitutionUseCase] invoke() - EXCEPTION!!! ${t.message}")
            CreateInstitutionOutcome.Error
        }
    }
}
