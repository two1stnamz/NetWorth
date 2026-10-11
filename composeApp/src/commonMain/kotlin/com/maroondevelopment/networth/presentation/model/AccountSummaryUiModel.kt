package com.maroondevelopment.networth.presentation.model

data class AccountSummaryUiModel(
    val id: Long,
    val title: String,
    val totalValue: String,
    val valueChange: String
)