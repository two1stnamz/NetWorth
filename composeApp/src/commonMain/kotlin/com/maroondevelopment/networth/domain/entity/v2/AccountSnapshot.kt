package com.maroondevelopment.networth.domain.entity.v2

data class AccountSnapshot(
    val account: Account,
    val assets: List<Asset>
) {

    /** The account's worth: every position valued at its quoted price. */
    val totalValue: Double get() = assets.sumOf { it.value }

    val totalValueChange: Double get() = assets.sumOf { it.valueChange }
}
