package com.maroondevelopment.networth.presentation.account

import com.maroondevelopment.networth.presentation.model.AccountSummaryUiModel
import com.maroondevelopment.networth.presentation.model.PositionUiModel

sealed interface AccountInfoUiState {

    data object Loading : AccountInfoUiState

    data object Error : AccountInfoUiState

    data class Loaded(
        val summary: AccountSummaryUiModel,
        val positions: List<PositionUiModel>
    ) : AccountInfoUiState
}