package com.maroondevelopment.networth.presentation.portfolio

import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot
import com.maroondevelopment.networth.presentation.model.AccountSummaryUiModel
import com.maroondevelopment.networth.presentation.model.PortfolioUiModel
import com.maroondevelopment.networth.util.roundTo2Decimals
import com.maroondevelopment.networth.util.toCurrency
import com.maroondevelopment.networth.util.toSignedCurrency

fun buildPortfolio(snapshots: List<AccountSnapshot>): PortfolioUiModel {
    val accounts = mutableListOf<AccountSummaryUiModel>()
    var totalValue = 0.0
    var totalChange = 0.0

    snapshots.forEach { snapshot ->
        val accountTotalValue = snapshot.assets.sumOf { it.quote.price * it.position.units }
        val accountTotalChange = snapshot.assets.sumOf { it.quote.valueChange * it.position.units }

        totalValue += accountTotalValue
        totalChange += accountTotalChange

        accounts.add(
            AccountSummaryUiModel(
                id = snapshot.account.id,
                title = snapshot.account.name,
                totalValue = accountTotalValue.roundTo2Decimals().toCurrency(),
                valueChange = accountTotalChange.roundTo2Decimals().toSignedCurrency()
            )
        )
    }

    return PortfolioUiModel(
        totalValue = totalValue.roundTo2Decimals().toCurrency(),
        totalChange = totalChange.roundTo2Decimals().toSignedCurrency(),
        accounts = accounts
    )
}
