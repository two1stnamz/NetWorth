package com.maroondevelopment.networth.domain.entity.v2

sealed interface RenameAccountOutcome {

    sealed interface Failure : RenameAccountOutcome {

        /** The new name is blank. */
        data object InvalidInput : Failure

        /** No account with this id — a card on screen has gone stale. */
        data object NotFound : Failure

        data class Technical(val error: String) : Failure
    }

    /** Carries the re-read institution so the caller shows the new name without reloading itself. */
    data class Success(val snapshot: InstitutionSnapshot) : RenameAccountOutcome
}
