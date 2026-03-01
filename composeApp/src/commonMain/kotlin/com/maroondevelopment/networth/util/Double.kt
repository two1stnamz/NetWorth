package com.maroondevelopment.networth.util

import kotlin.math.round

fun Double.roundTo2Decimals(): Double {
    return round(this * 100) / 100
}