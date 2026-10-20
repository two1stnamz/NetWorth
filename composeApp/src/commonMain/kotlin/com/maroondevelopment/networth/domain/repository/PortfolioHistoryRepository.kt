package com.maroondevelopment.networth.domain.repository

import com.maroondevelopment.networth.domain.entity.v2.PortfolioHistory
import com.maroondevelopment.networth.domain.entity.v2.PortfolioValuePoint
import kotlin.time.Instant

interface PortfolioHistoryRepository {

    /** Stores [point], replacing whatever was already recorded for its day. */
    suspend fun record(point: PortfolioValuePoint, recordedAt: Instant)

    suspend fun getHistory(): PortfolioHistory
}
