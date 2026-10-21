package com.maroondevelopment.networth.presentation.account

import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot
import com.maroondevelopment.networth.domain.entity.v2.Asset
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
        summary = toSummaryUiModel(),
        positions = assets.map { asset -> asset.toUiModel() },
        isRefreshing = isRefreshing,
        message = message
    )

/**
 * Renders one valued [Asset] as a position card.
 *
 * The heading is the instrument's name where the quote carried one, and the ticker otherwise; the
 * detail line drops the ticker when it is already the heading rather than printing it twice.
 */
private fun Asset.toUiModel(): PositionUiModel {
    val heading = quote.name ?: position.ticker
    val unitsLabel = position.units.toUnitsLabel()

    return PositionUiModel(
        ticker = position.ticker,
        displayName = heading,
        units = position.units,
        unitsLabel = unitsLabel,
        detail = listOfNotNull(
            position.ticker.takeIf { it != heading },
            "$unitsLabel units",
            quote.price.roundTo2Decimals().toCurrency()
        ).joinToString(" · "),
        currentValue = value.roundTo2Decimals().toCurrency(),
        valueChange = valueChange.roundTo2Decimals().toSignedCurrency(),
        isGain = valueChange >= 0.0
    )
}

/** The account's own headline figures, shared by the account card and the account screen. */
fun AccountSnapshot.toSummaryUiModel(): AccountSummaryUiModel =
    AccountSummaryUiModel(
        id = account.id,
        title = account.name,
        totalValue = totalValue.roundTo2Decimals().toCurrency(),
        valueChange = totalValueChange.roundTo2Decimals().toSignedCurrency(),
        isGain = totalValueChange >= 0.0,
        positionCount = assets.size
    )
