package com.maroondevelopment.networth.data.datasource

import com.maroondevelopment.networth.Database
import com.maroondevelopment.networth.data.model.PositionModel
import com.maroondevelopment.networth.data.model.Result

interface PositionDataSource {

    suspend fun getPositions(accountId: Long): Result<List<PositionModel>>

    suspend fun getPosition(accountId: Long, ticker: String): Result<PositionModel?>

    suspend fun insert(accountId: Long, position: PositionModel): Result<Unit>

    suspend fun updateUnits(accountId: Long, ticker: String, units: Double): Result<Unit>

    suspend fun delete(accountId: Long, ticker: String): Result<Unit>
}

class PositionDataSourceImpl(
    private val database: Database
) : PositionDataSource {

    override suspend fun getPositions(accountId: Long): Result<List<PositionModel>> =
        try {
            val positions = database.positionQueries
                .selectByAccount(accountId)
                .executeAsList()
                .mapNotNull { it.toModelOrNull() }

            Result.Success(positions)
        } catch (t: Throwable) {
            println("[BENG][PositionDataSourceImpl] getPositions() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    override suspend fun getPosition(accountId: Long, ticker: String): Result<PositionModel?> =
        try {
            val position = database.positionQueries
                .selectByAccountAndSymbol(accountId, ticker)
                .executeAsOneOrNull()
                ?.toModelOrNull()

            Result.Success(position)
        } catch (t: Throwable) {
            println("[BENG][PositionDataSourceImpl] getPosition() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    override suspend fun insert(accountId: Long, position: PositionModel): Result<Unit> =
        try {
            database.positionQueries.insert(accountId, position.ticker, position.units)
            Result.Success(Unit)
        } catch (t: Throwable) {
            println("[BENG][PositionDataSourceImpl] insert() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    override suspend fun updateUnits(accountId: Long, ticker: String, units: Double): Result<Unit> =
        try {
            database.positionQueries.updateUnits(units, accountId, ticker)
            Result.Success(Unit)
        } catch (t: Throwable) {
            println("[BENG][PositionDataSourceImpl] updateUnits() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    override suspend fun delete(accountId: Long, ticker: String): Result<Unit> =
        try {
            database.positionQueries.deleteByAccountAndSymbol(accountId, ticker)
            Result.Success(Unit)
        } catch (t: Throwable) {
            println("[BENG][PositionDataSourceImpl] delete() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    /** Rows with a null symbol or null units predate the current schema and are unusable. */
    private fun com.maroondevelopment.networth.Position.toModelOrNull(): PositionModel? {
        val symbol = symbol ?: return null
        val units = units ?: return null
        return PositionModel(ticker = symbol, units = units)
    }
}
