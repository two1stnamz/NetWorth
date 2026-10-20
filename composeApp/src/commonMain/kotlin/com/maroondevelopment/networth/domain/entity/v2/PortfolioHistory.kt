package com.maroondevelopment.networth.domain.entity.v2

/**
 * The recorded portfolio totals, oldest first.
 *
 * The derived figures live here so every caller reads the same numbers off one place.
 */
data class PortfolioHistory(
    val points: List<PortfolioValuePoint>
) {

    val earliest: PortfolioValuePoint? get() = points.firstOrNull()

    val latest: PortfolioValuePoint? get() = points.lastOrNull()

    val lowestValue: Double get() = points.minOfOrNull { it.totalValue } ?: 0.0

    val highestValue: Double get() = points.maxOfOrNull { it.totalValue } ?: 0.0

    /** Movement from the first recorded day to the most recent one. */
    val changeOverPeriod: Double
        get() {
            val first = earliest ?: return 0.0
            val last = latest ?: return 0.0
            return last.totalValue - first.totalValue
        }

    /** A line needs two points; one recorded day is not yet a trend. */
    val isPlottable: Boolean get() = points.size >= 2
}
