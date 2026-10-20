package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.v2.LoadPortfolioHistoryOutcome
import com.maroondevelopment.networth.domain.repository.PortfolioHistoryRepository

interface LoadPortfolioHistoryUseCase {

    suspend operator fun invoke(): LoadPortfolioHistoryOutcome
}

class LoadPortfolioHistoryUseCaseImpl(
    private val historyRepository: PortfolioHistoryRepository
) : LoadPortfolioHistoryUseCase {

    override suspend fun invoke(): LoadPortfolioHistoryOutcome =
        try {
            LoadPortfolioHistoryOutcome.Success(historyRepository.getHistory())
        } catch (t: Throwable) {
            println("[BENG][LoadPortfolioHistoryUseCase] invoke() - EXCEPTION!!! ${t.message}")
            LoadPortfolioHistoryOutcome.Error
        }
}
