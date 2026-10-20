package com.maroondevelopment.networth.domain.entity.v2

sealed interface LoadPortfolioHistoryOutcome {

    data class Success(val history: PortfolioHistory) : LoadPortfolioHistoryOutcome

    data object Error : LoadPortfolioHistoryOutcome
}
