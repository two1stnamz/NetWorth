package com.maroondevelopment.networth.presentation.model

data class AccountSummaryUiModel(
    val id: Long,
    val title: String,
    val totalValue: String,
    val valueChange: String,
    val isGain: Boolean,
    /** How many positions a delete would take with the account. */
    val positionCount: Int
)
