package com.maroondevelopment.networth.domain.time

import kotlin.time.Instant
import kotlinx.datetime.LocalDate

/**
 * The clock, behind an interface so use cases that stamp a date stay testable.
 */
interface TimeProvider {

    fun now(): Instant

    /** Today in the device's own time zone — the day a refresh is filed under. */
    fun today(): LocalDate
}
