package com.maroondevelopment.networth.data.datasource

import com.maroondevelopment.networth.Database
import com.maroondevelopment.networth.data.model.PortfolioValueModel
import com.maroondevelopment.networth.data.model.Result

interface PortfolioValueDataSource {

    suspend fun getAll(): Result<List<PortfolioValueModel>>

    /** Replaces the value already stored for [value]'s day, if there is one. */
    suspend fun insertOrReplace(value: PortfolioValueModel): Result<Unit>
}

class PortfolioValueDataSourceImpl(
    private val database: Database
) : PortfolioValueDataSource {

    override suspend fun getAll(): Result<List<PortfolioValueModel>> =
        try {
            val values = database.portfolioValueQueries
                .selectAll()
                .executeAsList()
                .map {
                    PortfolioValueModel(
                        day = it.day,
                        recordedAtMillis = it.recorded_at,
                        totalValue = it.total_value
                    )
                }

            Result.Success(values)
        } catch (t: Throwable) {
            println("[BENG][PortfolioValueDataSourceImpl] getAll() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    override suspend fun insertOrReplace(value: PortfolioValueModel): Result<Unit> =
        try {
            database.portfolioValueQueries.insertOrReplace(
                day = value.day,
                recorded_at = value.recordedAtMillis,
                total_value = value.totalValue
            )
            Result.Success(Unit)
        } catch (t: Throwable) {
            println("[BENG][PortfolioValueDataSourceImpl] insertOrReplace() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }
}
