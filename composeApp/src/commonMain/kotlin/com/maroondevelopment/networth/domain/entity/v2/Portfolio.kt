package com.maroondevelopment.networth.domain.entity.v2

/**
 * Every institution the user holds, valued as one whole.
 *
 * The totals are the sum of all positions across all accounts at all institutions, so callers
 * never have to re-derive the headline figure from the snapshots.
 */
data class Portfolio(
    val institutions: List<InstitutionSnapshot>
) {

    val totalValue: Double get() = institutions.sumOf { it.totalValue }

    val totalValueChange: Double get() = institutions.sumOf { it.totalValueChange }
}
