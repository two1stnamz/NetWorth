package com.maroondevelopment.networth.presentation.institution

import com.maroondevelopment.networth.presentation.model.AccountSummaryUiModel
import com.maroondevelopment.networth.presentation.model.InstitutionSummaryUiModel

sealed interface InstitutionUiState {

    data object Loading : InstitutionUiState

    data object Error : InstitutionUiState

    data class Loaded(
        val summary: InstitutionSummaryUiModel,
        val accounts: List<AccountSummaryUiModel>,
        /** A refresh is in flight over already-visible data. */
        val isRefreshing: Boolean = false,
        /** Transient feedback from the last add / rename / delete attempt. */
        val message: String? = null
    ) : InstitutionUiState
}
