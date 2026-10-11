package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.data.datasource.PositionDataSource
import com.maroondevelopment.networth.domain.entity.v2.NewPosition
import com.maroondevelopment.networth.domain.entity.v2.Position
import com.maroondevelopment.networth.domain.repository.PositionRepository

/**
 * [PositionRepository] backed by the holdings in `investments.json`, which all belong to
 * [AccountRepositoryImpl.DEFAULT_ACCOUNT].
 *
 * Placeholder until positions get their own persistence — writes are not persisted.
 */
class PositionRepositoryImpl(
    private val dataSource: PositionDataSource
) : PositionRepository {

    override suspend fun getPositionsForAccount(accountId: Long): List<Position> {
        return emptyList()
    }

    override suspend fun addPositionsForAccount(accountId: Long, positions: List<NewPosition>) {
        TODO("Positions are read-only until they have their own persistence")
    }

    override suspend fun updatePositionForAccount(accountId: Long, position: NewPosition) {
        TODO("Positions are read-only until they have their own persistence")
    }

    override suspend fun deletePositionForAccount(accountId: Long, ticker: String) {
        TODO("Positions are read-only until they have their own persistence")
    }
}
