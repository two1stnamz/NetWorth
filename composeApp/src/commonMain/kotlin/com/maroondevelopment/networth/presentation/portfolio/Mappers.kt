package com.maroondevelopment.networth.presentation.portfolio

import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot
import com.maroondevelopment.networth.presentation.model.PortfolioUiModel

fun buildPortfolio(snapshots: List<AccountSnapshot>): PortfolioUiModel {
    val totalValue = snapshots.sumOf { snapshot -> snapshot.assets.sumOf { it.position.units * it.quote.price } }
}