package com.maroondevelopment.networth.domain.entity

import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot


sealed interface LoadPortfolioOutcome {

    data class Success(val snapshots: List<AccountSnapshot>) : LoadPortfolioOutcome

    data object Error : LoadPortfolioOutcome
}