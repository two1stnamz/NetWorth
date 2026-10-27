package com.maroondevelopment.networth.presentation.model

data class PositionUiModel(
    val ticker: String,
    /** The instrument's name for the card heading, falling back to the ticker when unquoted. */
    val displayName: String,
    /** Raw units, kept so the edit sheet can pre-fill the field. */
    val units: Double,
    val unitsLabel: String,
    val currentValue: String,
    val valueChange: String,
    val isGain: Boolean
)
