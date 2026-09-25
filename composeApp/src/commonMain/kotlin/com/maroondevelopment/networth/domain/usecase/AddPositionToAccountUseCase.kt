package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.AddPositionOutcome
import com.maroondevelopment.networth.domain.entity.v2.NewPosition
import com.maroondevelopment.networth.domain.repository.PositionRepository

interface AddPositionToAccountUseCase {

    suspend operator fun invoke(accountId: String, position: NewPosition): AddPositionOutcome
}

class AddPositionToAccountUseCaseImpl(
    private val positionRepository: PositionRepository
): AddPositionToAccountUseCase {

    override suspend fun invoke(accountId: String, position: NewPosition): AddPositionOutcome {
        TODO("Not yet implemented")
    }
}