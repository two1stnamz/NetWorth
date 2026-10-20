package com.maroondevelopment.networth.presentation.model

data class InstitutionSummaryUiModel(
    val id: Long,
    val title: String,
    val totalValue: String,
    val valueChange: String,
    val isGain: Boolean,
    /** How many accounts a delete would take with the institution. */
    val accountCount: Int
)
