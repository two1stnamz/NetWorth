package com.maroondevelopment.networth.data.repository

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
    private val positionRepository: PositionRepository
) : PositionRepository {

    override suspend fun getPositionsForAccount(accountId: String): List<Position> {
        return positionRepository.getPositionsForAccount(accountId)
    }

    override suspend fun addPositionsForAccount(accountId: String, positions: List<NewPosition>) {
        TODO("Positions are read-only until they have their own persistence")
    }

    override suspend fun updatePositionForAccount(accountId: String, position: NewPosition) {
        TODO("Positions are read-only until they have their own persistence")
    }

    override suspend fun deletePositionForAccount(accountId: String, ticker: String) {
        TODO("Positions are read-only until they have their own persistence")
    }
}
