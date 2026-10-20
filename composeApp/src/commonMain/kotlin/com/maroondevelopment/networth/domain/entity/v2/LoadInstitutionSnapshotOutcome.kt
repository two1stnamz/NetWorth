package com.maroondevelopment.networth.domain.entity.v2

sealed interface LoadInstitutionSnapshotOutcome {

    sealed interface Failure : LoadInstitutionSnapshotOutcome {

        data object InstitutionNotFound : Failure

        data class Technical(val error: String) : Failure
    }

    data class Success(val snapshot: InstitutionSnapshot) : LoadInstitutionSnapshotOutcome
}
