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
): AccountInfoUiState.Loaded {
    var totalValue = 0.0
    var totalChange = 0.0

    val positions = assets.map { asset ->
        val value = (asset.quote.price * asset.position.units).roundTo2Decimals()
        val change = (asset.quote.valueChange * asset.position.units).roundTo2Decimals()

        totalValue += value
        totalChange += change

        PositionUiModel(
            ticker = asset.position.ticker,
            units = asset.position.units,
            unitsLabel = asset.position.units.toUnitsLabel(),
            currentValue = value.toCurrency(),
            valueChange = change.toSignedCurrency(),
            isGain = change >= 0.0
        )
    }

    return AccountInfoUiState.Loaded(
        summary = AccountSummaryUiModel(
            id = account.id,
            title = account.name,
            totalValue = totalValue.roundTo2Decimals().toCurrency(),
            valueChange = totalChange.roundTo2Decimals().toSignedCurrency()
        ),
        positions = positions,
        isGain = totalChange >= 0.0,
        isRefreshing = isRefreshing,
        message = message
    )
}
