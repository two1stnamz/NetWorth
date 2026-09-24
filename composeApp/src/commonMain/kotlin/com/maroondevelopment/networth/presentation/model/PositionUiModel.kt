package com.maroondevelopment.networth.presentation.model

data class PositionUiModel(
    val ticker: String,
    val name: String,
    val units: Double,
    val value: Double
)
