package com.maroondevelopment.networth.data.model

data class PortfolioValueModel(
    /** ISO-8601 calendar day, e.g. `2026-10-19`. */
    val day: String,
    val recordedAtMillis: Long,
    val totalValue: Double
)
