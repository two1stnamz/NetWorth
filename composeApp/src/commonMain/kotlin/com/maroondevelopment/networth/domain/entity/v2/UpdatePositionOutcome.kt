package com.maroondevelopment.networth.domain.entity.v2

sealed interface UpdatePositionOutcome {

    sealed interface Failure : UpdatePositionOutcome {

        /** Units are not a positive number. */
        data object InvalidInput : Failure

        /** The account does not hold this ticker. */
        data object NotFound : Failure

        data class Technical(val error: String) : Failure
    }

    data class Success(val snapshot: AccountSnapshot) : UpdatePositionOutcome
}
