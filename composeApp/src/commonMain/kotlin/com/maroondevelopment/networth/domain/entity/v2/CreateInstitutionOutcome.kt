package com.maroondevelopment.networth.domain.entity.v2

sealed interface CreateInstitutionOutcome {

    data object Success : CreateInstitutionOutcome

    data object Error : CreateInstitutionOutcome
}
