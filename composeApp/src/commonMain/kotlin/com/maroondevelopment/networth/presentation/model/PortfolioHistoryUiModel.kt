package com.maroondevelopment.networth.presentation.model

data class PortfolioHistoryUiModel(
    val latestValue: String,
    val changeLabel: String,
    val isGain: Boolean,
    /** e.g. `19 Sep – 19 Oct`, or a single day when only one value is recorded. */
    val rangeLabel: String,
    val highLabel: String,
    val lowLabel: String,
    val points: List<ChartPointUiModel>,
    /** Two or more points, so a line can actually be drawn. */
    val isPlottable: Boolean
)

data class ChartPointUiModel(
    val dayLabel: String,
    /** Raw total, for plotting. */
    val value: Double,
    val valueLabel: String
)
