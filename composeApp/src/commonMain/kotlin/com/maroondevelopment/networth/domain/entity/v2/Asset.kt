package com.maroondevelopment.networth.domain.entity.v2

import com.maroondevelopment.networth.domain.entity.Quote

data class Asset(
    val position: Position,
    val quote: Quote
)
