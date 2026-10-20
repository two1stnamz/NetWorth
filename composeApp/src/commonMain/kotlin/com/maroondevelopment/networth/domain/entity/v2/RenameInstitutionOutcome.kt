package com.maroondevelopment.networth.domain.entity.v2

sealed interface RenameInstitutionOutcome {

    sealed interface Failure : RenameInstitutionOutcome {

        /** The new name is blank. */
        data object InvalidInput : Failure

        /** No institution with this id — a card on screen has gone stale. */
        data object NotFound : Failure

        data class Technical(val error: String) : Failure
    }

    /** Carries the re-read portfolio so the caller shows the new name without reloading itself. */
    data class Success(val portfolio: Portfolio) : RenameInstitutionOutcome
}
