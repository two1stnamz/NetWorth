package com.maroondevelopment.networth.domain.entity

import com.maroondevelopment.networth.domain.entity.v2.Portfolio

sealed interface LoadPortfolioOutcome {

    data class Success(val portfolio: Portfolio) : LoadPortfolioOutcome

    data object Error : LoadPortfolioOutcome
}