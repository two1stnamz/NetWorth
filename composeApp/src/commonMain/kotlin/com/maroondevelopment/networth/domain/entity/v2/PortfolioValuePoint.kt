package com.maroondevelopment.networth.domain.entity.v2

import kotlinx.datetime.LocalDate

/** What the whole portfolio was worth on a given day. */
data class PortfolioValuePoint(
    val day: LocalDate,
    val totalValue: Double
)
