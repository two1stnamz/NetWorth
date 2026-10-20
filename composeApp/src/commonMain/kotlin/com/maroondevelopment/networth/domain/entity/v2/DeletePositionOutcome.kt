package com.maroondevelopment.networth.domain.entity.v2

sealed interface DeletePositionOutcome {

    sealed interface Failure : DeletePositionOutcome {

        /** The account does not hold this ticker. */
        data object NotFound : Failure

        data class Technical(val error: String) : Failure
    }

    data class Success(val snapshot: AccountSnapshot) : DeletePositionOutcome
}
