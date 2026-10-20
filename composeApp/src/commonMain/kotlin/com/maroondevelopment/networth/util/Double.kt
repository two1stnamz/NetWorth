package com.maroondevelopment.networth.util

import kotlin.math.abs
import kotlin.math.round

private const val DOLLAR = '$'

fun Double.roundTo2Decimals(): Double {
    return round(this * 100) / 100
}

/** `1234.5` -> `"$1,234.50"`. */
fun Double.toCurrency(): String = formatCurrency(this, signed = false)

/** `1234.5` -> `"+$1,234.50"`, `-12.0` -> `"-$12.00"`. */
fun Double.toSignedCurrency(): String = formatCurrency(this, signed = true)

/** Drops a meaningless trailing `.0`: `12.0` -> `"12"`, `12.5` -> `"12.5"`. */
fun Double.toUnitsLabel(): String {
    val rounded = roundTo2Decimals()
    return if (rounded == round(rounded)) rounded.toLong().toString() else rounded.toString()
}

/**
 * Formats to two decimal places with thousands separators.
 *
 * Hand-rolled because `commonMain` has no locale-aware number formatter; amounts are always
 * rendered as US dollars, matching the quotes the app fetches.
 */
private fun formatCurrency(value: Double, signed: Boolean): String {
    val cents = round(abs(value) * 100).toLong()
    val whole = (cents / 100).toString()
    val fraction = (cents % 100).toString().padStart(2, '0')

    val grouped = whole
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()

    val sign = when {
        cents == 0L -> ""
        value < 0.0 -> "-"
        signed -> "+"
        else -> ""
    }

    return "$sign$DOLLAR$grouped.$fraction"
}
