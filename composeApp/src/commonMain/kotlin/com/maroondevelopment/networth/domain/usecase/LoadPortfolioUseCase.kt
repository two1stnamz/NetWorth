package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.LoadPortfolioOutcome
import com.maroondevelopment.networth.domain.entity.LoadStrategy

interface LoadPortfolioUseCase {

    suspend operator fun invoke(strategy: LoadStrategy): LoadPortfolioOutcome
}

class LoadPortfolioUseCaseImpl: LoadPortfolioUseCase {

    override suspend fun invoke(strategy: LoadStrategy): LoadPortfolioOutcome {
        TODO("Not yet implemented")
    }
}