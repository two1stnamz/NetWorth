package com.maroondevelopment.networth.presentation.institution

import com.maroondevelopment.networth.domain.entity.v2.InstitutionSnapshot
import com.maroondevelopment.networth.presentation.account.toSummaryUiModel
import com.maroondevelopment.networth.presentation.model.InstitutionSummaryUiModel
import com.maroondevelopment.networth.util.roundTo2Decimals
import com.maroondevelopment.networth.util.toCurrency
import com.maroondevelopment.networth.util.toSignedCurrency

/** Renders an [InstitutionSnapshot] as the institution screen's loaded state. */
fun InstitutionSnapshot.toLoadedState(
    isRefreshing: Boolean = false,
    message: String? = null
): InstitutionUiState.Loaded =
    InstitutionUiState.Loaded(
        summary = toSummaryUiModel(),
        accounts = accounts.map { it.toSummaryUiModel() },
        isRefreshing = isRefreshing,
        message = message
    )

/** The institution's own headline figures, shared by its card and its screen. */
fun InstitutionSnapshot.toSummaryUiModel(): InstitutionSummaryUiModel =
    InstitutionSummaryUiModel(
        id = institution.id,
        title = institution.name,
        totalValue = totalValue.roundTo2Decimals().toCurrency(),
        valueChange = totalValueChange.roundTo2Decimals().toSignedCurrency(),
        isGain = totalValueChange >= 0.0,
        accountCount = accounts.size
    )
