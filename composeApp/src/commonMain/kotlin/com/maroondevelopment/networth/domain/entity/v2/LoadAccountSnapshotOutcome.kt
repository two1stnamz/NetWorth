package com.maroondevelopment.networth.domain.entity.v2

sealed interface LoadAccountSnapshotOutcome {

    sealed interface Failure : LoadAccountSnapshotOutcome {

        data object AccountNotFound : Failure

        data class Technical(val error: String) : Failure
    }

    data class Success(val snapshot: AccountSnapshot) : LoadAccountSnapshotOutcome
}
