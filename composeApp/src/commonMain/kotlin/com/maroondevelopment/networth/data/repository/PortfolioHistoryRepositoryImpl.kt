package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.data.datasource.PortfolioValueDataSource
import com.maroondevelopment.networth.data.model.PortfolioValueModel
import com.maroondevelopment.networth.data.model.getOrNull
import com.maroondevelopment.networth.data.model.getOrThrow
import com.maroondevelopment.networth.domain.entity.v2.PortfolioHistory
import com.maroondevelopment.networth.domain.entity.v2.PortfolioValuePoint
import com.maroondevelopment.networth.domain.repository.PortfolioHistoryRepository
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

class PortfolioHistoryRepositoryImpl(
    private val dataSource: PortfolioValueDataSource
) : PortfolioHistoryRepository {

    override suspend fun record(point: PortfolioValuePoint, recordedAt: Instant) {
        dataSource.insertOrReplace(
            PortfolioValueModel(
                day = point.day.toString(),
                recordedAtMillis = recordedAt.toEpochMilliseconds(),
                totalValue = point.totalValue
            )
        ).getOrThrow()
    }

    override suspend fun getHistory(): PortfolioHistory {
        val points = dataSource.getAll().getOrNull()
            ?.mapNotNull { it.toPointOrNull() }
            ?: emptyList()

        return PortfolioHistory(points.sortedBy { it.day })
    }

    /** A row whose day is not a valid date predates the current schema and is skipped. */
    private fun PortfolioValueModel.toPointOrNull(): PortfolioValuePoint? =
        try {
            PortfolioValuePoint(day = LocalDate.parse(day), totalValue = totalValue)
        } catch (t: Throwable) {
            println("[BENG][PortfolioHistoryRepositoryImpl] bad day '$day' - ${t.message}")
            null
        }
}
