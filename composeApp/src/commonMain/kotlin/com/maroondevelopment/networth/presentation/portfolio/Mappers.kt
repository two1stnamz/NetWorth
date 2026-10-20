package com.maroondevelopment.networth.presentation.portfolio

import com.maroondevelopment.networth.domain.entity.v2.Portfolio
import com.maroondevelopment.networth.presentation.model.AccountSummaryUiModel
import com.maroondevelopment.networth.presentation.model.PortfolioUiModel
import com.maroondevelopment.networth.util.roundTo2Decimals
import com.maroondevelopment.networth.util.toCurrency
import com.maroondevelopment.networth.util.toSignedCurrency

/** Formats the already-valued [Portfolio] for display; all summing happens in the domain. */
fun Portfolio.toUiModel(): PortfolioUiModel =
    PortfolioUiModel(
        totalValue = totalValue.roundTo2Decimals().toCurrency(),
        totalChange = totalValueChange.roundTo2Decimals().toSignedCurrency(),
        accounts = accounts.map { snapshot ->
            AccountSummaryUiModel(
                id = snapshot.account.id,
                title = snapshot.account.name,
                totalValue = snapshot.totalValue.roundTo2Decimals().toCurrency(),
                valueChange = snapshot.totalValueChange.roundTo2Decimals().toSignedCurrency()
            )
        }
    )
