package com.maroondevelopment.networth.domain.repository

import com.maroondevelopment.networth.domain.entity.v2.NewPosition
import com.maroondevelopment.networth.domain.entity.v2.Position

interface PositionRepository {

    suspend fun getPositionsForAccount(accountId: String): List<Position>

    suspend fun addPositionsForAccount(accountId: String, positions: List<NewPosition>)

    suspend fun updatePositionForAccount(accountId: String, position: NewPosition)

    suspend fun deletePositionForAccount(accountId: String, ticker: String)
}