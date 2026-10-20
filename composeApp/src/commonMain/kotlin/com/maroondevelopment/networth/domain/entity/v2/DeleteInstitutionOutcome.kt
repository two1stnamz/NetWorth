package com.maroondevelopment.networth.domain.entity.v2

sealed interface DeleteInstitutionOutcome {

    sealed interface Failure : DeleteInstitutionOutcome {

        /** No institution with this id — a card on screen has gone stale. */
        data object NotFound : Failure

        data class Technical(val error: String) : Failure
    }

    /** Carries the re-read portfolio, now missing the deleted institution. */
    data class Success(val portfolio: Portfolio) : DeleteInstitutionOutcome
}
