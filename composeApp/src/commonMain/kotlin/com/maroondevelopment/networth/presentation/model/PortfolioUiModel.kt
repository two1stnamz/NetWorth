package com.maroondevelopment.networth.presentation.model

data class PortfolioUiModel(
    val totalValue: String,
    val totalChange: String,
    val accounts: List<AccountSummaryUiModel>
)
