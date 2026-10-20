package com.maroondevelopment.networth.domain.entity.v2

/**
 * An institution with each of its accounts valued.
 *
 * The totals span every account held at the institution.
 */
data class InstitutionSnapshot(
    val institution: Institution,
    val accounts: List<AccountSnapshot>
) {

    val totalValue: Double get() = accounts.sumOf { it.totalValue }

    val totalValueChange: Double get() = accounts.sumOf { it.totalValueChange }
}
