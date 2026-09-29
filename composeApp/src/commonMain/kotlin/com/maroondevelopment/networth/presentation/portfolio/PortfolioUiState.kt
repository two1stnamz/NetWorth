package com.maroondevelopment.networth.presentation.portfolio

import com.maroondevelopment.networth.presentation.model.PortfolioUiModel

sealed interface PortfolioUiState {

    data object Loading : PortfolioUiState

    data class Success(val model: PortfolioUiModel): PortfolioUiState

    data object Error : PortfolioUiState
}