package com.maroondevelopment.networth.presentation.portfolio

import com.maroondevelopment.networth.presentation.model.PortfolioUiModel

sealed interface PortfolioUiState {

    data object Loading : PortfolioUiState

    data class Success(
        val model: PortfolioUiModel,
        /** Transient feedback from the last add / rename / delete attempt. */
        val message: String? = null
    ) : PortfolioUiState

    data object Error : PortfolioUiState
}
