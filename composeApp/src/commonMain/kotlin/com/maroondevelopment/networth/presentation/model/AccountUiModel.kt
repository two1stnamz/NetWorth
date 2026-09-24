package com.maroondevelopment.networth.presentation.model

data class AccountUiModel(
    val id: String,
    val title: String,
    val totalValue: String,
    val positions: List<PositionUiModel>
)
