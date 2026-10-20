package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.data.datasource.InstitutionDataSource
import com.maroondevelopment.networth.data.model.getOrNull
import com.maroondevelopment.networth.data.model.getOrThrow
import com.maroondevelopment.networth.domain.entity.v2.Institution
import com.maroondevelopment.networth.domain.repository.InstitutionRepository

class InstitutionRepositoryImpl(
    private val dataSource: InstitutionDataSource
) : InstitutionRepository {

    override suspend fun getInstitution(institutionId: Long): Institution? =
        dataSource.get(institutionId).getOrNull()?.let { Institution(it.id, it.name) }

    override suspend fun fetchInstitutions(): List<Institution> =
        dataSource.getAll().getOrNull()?.map { Institution(it.id, it.name) } ?: emptyList()

    override suspend fun createInstitution(name: String) {
        dataSource.insert(name).getOrThrow()
    }

    override suspend fun renameInstitution(institutionId: Long, name: String) {
        dataSource.rename(institutionId, name).getOrThrow()
    }

    override suspend fun deleteInstitution(institutionId: Long) {
        dataSource.delete(institutionId).getOrThrow()
    }
}
