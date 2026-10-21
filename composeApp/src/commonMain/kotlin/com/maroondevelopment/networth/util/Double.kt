package com.maroondevelopment.networth.util

import kotlin.math.abs
import kotlin.math.round

private const val DOLLAR = '$'

/** Holdings are quoted in fractional shares, so units carry four decimal places. */
private const val UNITS_DECIMALS = 4
private const val UNITS_SCALE = 10_000.0

fun Double.roundTo2Decimals(): Double {
    return round(this * 100) / 100
}

/**
 * Rounds to the four decimal places units are held at.
 *
 * Applied before units are stored, so a saved holding always matches the [toUnitsLabel] the
 * screen shows it as, and re-saving an untouched field cannot drift the value.
 */
fun Double.roundTo4Decimals(): Double {
    return round(this * UNITS_SCALE) / UNITS_SCALE
}

/** `1234.5` -> `"$1,234.50"`. */
fun Double.toCurrency(): String = formatCurrency(this, signed = false)

/** `1234.5` -> `"+$1,234.50"`, `-12.0` -> `"-$12.00"`. */
fun Double.toSignedCurrency(): String = formatCurrency(this, signed = true)

/**
 * Units to at most four decimal places, with meaningless trailing zeros dropped:
 * `12.0` -> `"12"`, `12.5` -> `"12.5"`, `0.1234` -> `"0.1234"`, `0.0001` -> `"0.0001"`.
 *
 * Built from the digits rather than `toString()`, which switches to scientific notation on small
 * and large values (`0.0001` would print as `1.0E-4`). This is also what the edit field is
 * pre-filled with, so the precision shown here is the precision a re-save preserves.
 */
fun Double.toUnitsLabel(): String {
    val scaled = round(abs(this) * UNITS_SCALE).toLong()
    val whole = scaled / UNITS_SCALE.toLong()
    val fraction = (scaled % UNITS_SCALE.toLong())
        .toString()
        .padStart(UNITS_DECIMALS, '0')
        .trimEnd('0')
    val sign = if (this < 0.0 && scaled != 0L) "-" else ""

    return if (fraction.isEmpty()) "$sign$whole" else "$sign$whole.$fraction"
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
