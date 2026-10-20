package com.maroondevelopment.networth.presentation.history

import com.maroondevelopment.networth.presentation.model.PortfolioHistoryUiModel

sealed interface PortfolioHistoryUiState {

    data object Loading : PortfolioHistoryUiState

    data object Error : PortfolioHistoryUiState

    /** Nothing recorded yet — the user has not refreshed the portfolio. */
    data object Empty : PortfolioHistoryUiState

    data class Loaded(val model: PortfolioHistoryUiModel) : PortfolioHistoryUiState
}
