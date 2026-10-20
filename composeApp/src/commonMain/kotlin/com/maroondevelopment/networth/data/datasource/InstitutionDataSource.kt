package com.maroondevelopment.networth.data.datasource

import com.maroondevelopment.networth.Database
import com.maroondevelopment.networth.data.model.InstitutionModel
import com.maroondevelopment.networth.data.model.Result

interface InstitutionDataSource {

    suspend fun getAll(): Result<List<InstitutionModel>>

    suspend fun get(id: Long): Result<InstitutionModel?>

    suspend fun insert(name: String): Result<Unit>

    suspend fun rename(id: Long, name: String): Result<Unit>

    /** Also removes every account held at the institution, and those accounts' positions. */
    suspend fun delete(id: Long): Result<Unit>
}

class InstitutionDataSourceImpl(
    private val database: Database
) : InstitutionDataSource {

    override suspend fun getAll(): Result<List<InstitutionModel>> =
        try {
            Result.Success(
                database.institutionQueries
                    .selectAll()
                    .executeAsList()
                    .map { InstitutionModel(it.id, it.name) }
            )
        } catch (t: Throwable) {
            println("[BENG][InstitutionDataSourceImpl] getAll() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    override suspend fun get(id: Long): Result<InstitutionModel?> =
        try {
            Result.Success(
                database.institutionQueries
                    .get(id)
                    .executeAsOneOrNull()
                    ?.let { InstitutionModel(it.id, it.name) }
            )
        } catch (t: Throwable) {
            println("[BENG][InstitutionDataSourceImpl] get() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    override suspend fun insert(name: String): Result<Unit> =
        try {
            database.institutionQueries.insert(name)
            Result.Success(Unit)
        } catch (t: Throwable) {
            println("[BENG][InstitutionDataSourceImpl] insert() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    override suspend fun rename(id: Long, name: String): Result<Unit> =
        try {
            database.institutionQueries.updateName(name = name, id = id)
            Result.Success(Unit)
        } catch (t: Throwable) {
            println("[BENG][InstitutionDataSourceImpl] rename() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }

    /**
     * The `institution_id` and `account_id` foreign keys carry no `ON DELETE CASCADE`, so the
     * children are cleared here — innermost first, and in one transaction so a failure part way
     * through cannot leave accounts or positions pointing at an institution that is gone.
     */
    override suspend fun delete(id: Long): Result<Unit> =
        try {
            database.transaction {
                database.positionQueries.deleteByInstitution(id)
                database.accountQueries.deleteByInstitution(id)
                database.institutionQueries.deleteById(id)
            }
            Result.Success(Unit)
        } catch (t: Throwable) {
            println("[BENG][InstitutionDataSourceImpl] delete() - EXCEPTION!!! ${t.message}")
            Result.Error.Storage(t)
        }
}
