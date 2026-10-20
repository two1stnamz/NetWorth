package com.maroondevelopment.networth.domain.entity.v2

sealed interface DeleteAccountOutcome {

    sealed interface Failure : DeleteAccountOutcome {

        /** No account with this id — a card on screen has gone stale. */
        data object NotFound : Failure

        data class Technical(val error: String) : Failure
    }

    /** Carries the re-read institution, now missing the deleted account. */
    data class Success(val snapshot: InstitutionSnapshot) : DeleteAccountOutcome
}
