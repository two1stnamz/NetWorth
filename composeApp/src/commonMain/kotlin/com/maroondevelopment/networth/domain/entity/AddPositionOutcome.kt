package com.maroondevelopment.networth.domain.entity

import com.maroondevelopment.networth.domain.entity.v2.AccountSnapshot

sealed interface AddPositionOutcome {

    sealed interface Failure : AddPositionOutcome {

        /** Blank ticker, or units that are not a positive number. */
        data object InvalidInput : Failure

        /** The account already holds this ticker — edit the existing position instead. */
        data object DuplicateTicker : Failure

        /** No tradeable quote came back for the ticker. */
        data object NoQuote : Failure

        data class Technical(val error: String) : Failure
    }

    data class Success(val snapshot: AccountSnapshot) : AddPositionOutcome
}
