package com.maroondevelopment.networth.domain.entity.v2

sealed interface CreateAccountOutcome {

    data object Success : CreateAccountOutcome

    data object Error : CreateAccountOutcome

}
