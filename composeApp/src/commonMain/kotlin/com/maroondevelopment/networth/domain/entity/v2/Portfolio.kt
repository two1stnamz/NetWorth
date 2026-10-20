package com.maroondevelopment.networth.domain.entity.v2

/**
 * Every account the user holds, valued as one whole.
 *
 * The totals are the sum of all positions across all accounts, so callers never have to
 * re-derive the headline figure from the snapshots.
 */
data class Portfolio(
    val accounts: List<AccountSnapshot>
) {

    val totalValue: Double get() = accounts.sumOf { it.totalValue }

    val totalValueChange: Double get() = accounts.sumOf { it.totalValueChange }
}
