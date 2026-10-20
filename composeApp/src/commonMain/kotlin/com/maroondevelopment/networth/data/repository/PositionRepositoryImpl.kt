package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.data.datasource.PositionDataSource
import com.maroondevelopment.networth.data.model.PositionModel
import com.maroondevelopment.networth.data.model.getOrNull
import com.maroondevelopment.networth.data.model.getOrThrow
import com.maroondevelopment.networth.domain.entity.v2.NewPosition
import com.maroondevelopment.networth.domain.entity.v2.Position
import com.maroondevelopment.networth.domain.repository.PositionRepository

/**
 * [PositionRepository] backed by the `Position` table, scoped by `account_id`.
 *
 * Reads degrade to an empty list so a storage failure cannot take the screen down, while writes
 * throw [com.maroondevelopment.networth.data.model.DataSourceException] so the calling use case
 * can report the failure instead of silently claiming success.
 */
class PositionRepositoryImpl(
    private val dataSource: PositionDataSource
) : PositionRepository {

    override suspend fun getPositionsForAccount(accountId: Long): List<Position> =
        dataSource.getPositions(accountId).getOrNull()
            ?.map { Position(ticker = it.ticker, units = it.units) }
            ?: emptyList()

    override suspend fun addPositionsForAccount(accountId: Long, positions: List<NewPosition>) {
        positions.forEach { position ->
            dataSource.insert(accountId, PositionModel(position.ticker, position.units)).getOrThrow()
        }
    }

    override suspend fun updatePositionForAccount(accountId: Long, position: NewPosition) {
        dataSource.updateUnits(accountId, position.ticker, position.units).getOrThrow()
    }

    override suspend fun deletePositionForAccount(accountId: Long, ticker: String) {
        dataSource.delete(accountId, ticker).getOrThrow()
    }
}
