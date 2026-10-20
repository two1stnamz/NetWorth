package com.maroondevelopment.networth.presentation.history

import com.maroondevelopment.networth.domain.entity.v2.PortfolioHistory
import com.maroondevelopment.networth.presentation.model.ChartPointUiModel
import com.maroondevelopment.networth.presentation.model.PortfolioHistoryUiModel
import com.maroondevelopment.networth.util.roundTo2Decimals
import com.maroondevelopment.networth.util.toCurrency
import com.maroondevelopment.networth.util.toSignedCurrency
import kotlinx.datetime.LocalDate

private val MONTH_ABBREVIATIONS =
    listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

/** Formats the already-derived [PortfolioHistory] for display. */
fun PortfolioHistory.toUiModel(): PortfolioHistoryUiModel =
    PortfolioHistoryUiModel(
        latestValue = (latest?.totalValue ?: 0.0).roundTo2Decimals().toCurrency(),
        changeLabel = changeOverPeriod.roundTo2Decimals().toSignedCurrency(),
        isGain = changeOverPeriod >= 0.0,
        rangeLabel = rangeLabel(),
        highLabel = highestValue.roundTo2Decimals().toCurrency(),
        lowLabel = lowestValue.roundTo2Decimals().toCurrency(),
        points = points.map { point ->
            ChartPointUiModel(
                dayLabel = point.day.toShortLabel(),
                value = point.totalValue,
                valueLabel = point.totalValue.roundTo2Decimals().toCurrency()
            )
        },
        isPlottable = isPlottable
    )

private fun PortfolioHistory.rangeLabel(): String {
    val from = earliest?.day ?: return ""
    val to = latest?.day ?: return ""

    return if (from == to) from.toShortLabel() else "${from.toShortLabel()} – ${to.toShortLabel()}"
}

private fun LocalDate.toShortLabel(): String = "$dayOfMonth ${MONTH_ABBREVIATIONS[monthNumber - 1]}"
