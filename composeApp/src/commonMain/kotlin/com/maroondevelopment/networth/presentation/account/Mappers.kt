package com.maroondevelopment.networth.presentation.account

import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot
import com.maroondevelopment.networth.presentation.model.AccountSummaryUiModel
import com.maroondevelopment.networth.presentation.model.PositionUiModel
import com.maroondevelopment.networth.util.roundTo2Decimals
import com.maroondevelopment.networth.util.toCurrency
import com.maroondevelopment.networth.util.toSignedCurrency
import com.maroondevelopment.networth.util.toUnitsLabel

/** Renders an [AccountSnapshot] as the account screen's loaded state. */
fun AccountSnapshot.toLoadedState(
    isRefreshing: Boolean = false,
    message: String? = null
): AccountInfoUiState.Loaded =
    AccountInfoUiState.Loaded(
        summary = AccountSummaryUiModel(
            id = account.id,
            title = account.name,
            totalValue = totalValue.roundTo2Decimals().toCurrency(),
            valueChange = totalValueChange.roundTo2Decimals().toSignedCurrency()
        ),
        positions = assets.map { asset ->
            PositionUiModel(
                ticker = asset.position.ticker,
                units = asset.position.units,
                unitsLabel = asset.position.units.toUnitsLabel(),
                currentValue = asset.value.roundTo2Decimals().toCurrency(),
                valueChange = asset.valueChange.roundTo2Decimals().toSignedCurrency(),
                isGain = asset.valueChange >= 0.0
            )
        },
        isGain = totalValueChange >= 0.0,
        isRefreshing = isRefreshing,
        message = message
    )
