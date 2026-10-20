package com.maroondevelopment.networth.domain.usecase

import com.maroondevelopment.networth.domain.entity.v2.Portfolio
import com.maroondevelopment.networth.domain.entity.v2.PortfolioValuePoint
import com.maroondevelopment.networth.domain.repository.PortfolioHistoryRepository
import com.maroondevelopment.networth.domain.time.TimeProvider

interface RecordPortfolioValueUseCase {

    suspend operator fun invoke(portfolio: Portfolio): Boolean
}

/**
 * Files a portfolio's total under today's date.
 *
 * One value per day: refreshing again today replaces today's figure rather than adding a second
 * point, so the history reads as one measurement per day.
 */
class RecordPortfolioValueUseCaseImpl(
    private val historyRepository: PortfolioHistoryRepository,
    private val timeProvider: TimeProvider
) : RecordPortfolioValueUseCase {

    override suspend fun invoke(portfolio: Portfolio): Boolean =
        try {
            historyRepository.record(
                point = PortfolioValuePoint(
                    day = timeProvider.today(),
                    totalValue = portfolio.totalValue
                ),
                recordedAt = timeProvider.now()
            )
            true
        } catch (t: Throwable) {
            println("[BENG][RecordPortfolioValueUseCase] invoke() - EXCEPTION!!! ${t.message}")
            false
        }
}
