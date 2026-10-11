package com.maroondevelopment.networth.domain.repository

import com.maroondevelopment.networth.domain.entity.v2.NewPosition
import com.maroondevelopment.networth.domain.entity.v2.Position

interface PositionRepository {

    suspend fun getPositionsForAccount(accountId: Long): List<Position>

    suspend fun addPositionsForAccount(accountId: Long, positions: List<NewPosition>)

    suspend fun updatePositionForAccount(accountId: Long, position: NewPosition)

    suspend fun deletePositionForAccount(accountId: Long, ticker: String)
}