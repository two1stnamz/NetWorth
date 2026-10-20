package com.maroondevelopment.networth.domain.repository

import com.maroondevelopment.networth.domain.entity.v2.Institution

interface InstitutionRepository {

    suspend fun getInstitution(institutionId: Long): Institution?

    suspend fun fetchInstitutions(): List<Institution>

    suspend fun createInstitution(name: String)

    suspend fun renameInstitution(institutionId: Long, name: String)

    /** Also removes the institution's accounts and their positions. */
    suspend fun deleteInstitution(institutionId: Long)
}
