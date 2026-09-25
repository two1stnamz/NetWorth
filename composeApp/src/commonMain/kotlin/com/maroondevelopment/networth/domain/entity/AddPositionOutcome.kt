package com.maroondevelopment.networth.domain.entity

import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot

sealed interface AddPositionOutcome {

    sealed interface Failure : AddPositionOutcome {

        data object NoQuote : Failure

        data class Technical(val error: String) : Failure
    }

    data class Success(val snapshot: AccountSnapshot) : AddPositionOutcome
}