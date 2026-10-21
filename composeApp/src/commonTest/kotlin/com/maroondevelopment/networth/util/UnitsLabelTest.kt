package com.maroondevelopment.networth.util

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The units label is what the edit field is pre-filled with, so whatever it drops is lost on the
 * next save. These cases pin the supported precision at four decimal places.
 */
class UnitsLabelTest {

    @Test
    fun whole_units_lose_the_trailing_zero() {
        assertEquals("12", 12.0.toUnitsLabel())
        assertEquals("0", 0.0.toUnitsLabel())
    }

    @Test
    fun trailing_zeros_are_dropped_but_the_digits_are_kept() {
        assertEquals("12.5", 12.5.toUnitsLabel())
        assertEquals("12.34", 12.3400.toUnitsLabel())
    }

    @Test
    fun four_decimal_places_survive() {
        assertEquals("0.1234", 0.1234.toUnitsLabel())
        assertEquals("12.3456", 12.3456.toUnitsLabel())
    }

    @Test
    fun small_values_do_not_fall_back_to_scientific_notation() {
        assertEquals("0.0001", 0.0001.toUnitsLabel())
        assertEquals("0.001", 0.001.toUnitsLabel())
    }

    @Test
    fun rounding_for_storage_agrees_with_the_label() {
        // What is stored and what is shown have to match, or re-saving an untouched field drifts.
        listOf(12.0, 12.5, 0.1234, 0.0001, 0.123456, 7.99999).forEach { typed ->
            val stored = typed.roundTo4Decimals()
            assertEquals(stored.toUnitsLabel(), typed.toUnitsLabel())
            assertEquals(stored, stored.toUnitsLabel().toDouble())
        }
    }

    @Test
    fun units_too_small_to_show_round_away_to_zero() {
        // The add and update use cases check this after rounding, so such input is rejected
        // instead of being stored as a zero-unit holding.
        assertEquals(0.0, 0.00001.roundTo4Decimals())
    }

    @Test
    fun a_fifth_decimal_place_rounds() {
        assertEquals("1", 0.99999.toUnitsLabel())
        assertEquals("0.1235", 0.123451.toUnitsLabel())

        // A literal ending in an exact half is decided by the double actually stored, not by the
        // digits typed: 0.12345 is held as 0.1234499999..., so it rounds down.
        assertEquals("0.1234", 0.12345.toUnitsLabel())
    }
}
