package com.maroondevelopment.networth.data.time

import com.maroondevelopment.networth.domain.time.TimeProvider
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SystemTimeProvider : TimeProvider {

    override fun now(): Instant = Clock.System.now()

    override fun today(): LocalDate =
        now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}
