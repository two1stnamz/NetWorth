package com.maroondevelopment.networth.domain.entity.v2

import com.maroondevelopment.networth.domain.entity.Quote

data class Asset(
    val position: Position,
    val quote: Quote
) {

    /** What the held units are worth at the quoted price. */
    val value: Double get() = quote.price * position.units

    /** How much that value moved, over the period the quote's change covers. */
    val valueChange: Double get() = quote.valueChange * position.units
}
