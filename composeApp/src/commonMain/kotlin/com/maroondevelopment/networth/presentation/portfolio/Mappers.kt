package com.maroondevelopment.networth.presentation.portfolio

import com.maroondevelopment.networth.domain.entity.v2.Portfolio
import com.maroondevelopment.networth.presentation.institution.toSummaryUiModel
import com.maroondevelopment.networth.presentation.model.PortfolioUiModel
import com.maroondevelopment.networth.util.roundTo2Decimals
import com.maroondevelopment.networth.util.toCurrency
import com.maroondevelopment.networth.util.toSignedCurrency

/** Renders an already-valued [Portfolio] as the portfolio screen's loaded state. */
fun Portfolio.toSuccessState(message: String? = null): PortfolioUiState.Success =
    PortfolioUiState.Success(model = toUiModel(), message = message)

/** Formats the already-valued [Portfolio] for display; all summing happens in the domain. */
fun Portfolio.toUiModel(): PortfolioUiModel =
    PortfolioUiModel(
        totalValue = totalValue.roundTo2Decimals().toCurrency(),
        totalChange = totalValueChange.roundTo2Decimals().toSignedCurrency(),
        institutions = institutions.map { it.toSummaryUiModel() }
    )
