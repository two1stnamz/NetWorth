package com.maroondevelopment.networth.presentation.portfolio

import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot
import com.maroondevelopment.networth.presentation.model.AccountSummaryUiModel
import com.maroondevelopment.networth.presentation.model.PortfolioUiModel

fun buildPortfolio(snapshots: List<AccountSnapshot>): PortfolioUiModel {
    val accounts = mutableListOf<AccountSummaryUiModel>()
    var totalValue = 0.0
    var totalChange = 0.0

    snapshots.forEach { snapshot ->
        val accountTotalValue = snapshot.assets.sumOf { it.quote.price * it.position.units }
        val accountTotalChange = snapshot.assets.sumOf { it.quote.valueChange * it.position.units }

        totalValue += accountTotalValue
        totalChange += accountTotalChange

        accounts.add(AccountSummaryUiModel(snapshot.account.id, snapshot.account.name, "$$totalValue", "$$totalChange"))
    }

    return PortfolioUiModel("$$totalValue", "$$totalChange", accounts)
}