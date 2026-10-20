package com.maroondevelopment.networth.presentation.account

import com.maroondevelopment.networth.presentation.model.AccountSummaryUiModel
import com.maroondevelopment.networth.presentation.model.PositionUiModel

sealed interface AccountInfoUiState {

    data object Loading : AccountInfoUiState

    data object Error : AccountInfoUiState

    data class Loaded(
        val summary: AccountSummaryUiModel,
        val positions: List<PositionUiModel>,
        /** A refresh is in flight over already-visible data. */
        val isRefreshing: Boolean = false,
        /** Transient feedback from the last add / edit / delete attempt. */
        val message: String? = null
    ) : AccountInfoUiState
}
