package com.maroondevelopment.networth.data.repository

import com.maroondevelopment.networth.domain.entity.Holding
import com.maroondevelopment.networth.domain.repository.HoldingsRepository
import kotlinx.serialization.json.Json
import networth.composeapp.generated.resources.Res

class HoldingsRepositoryImpl : HoldingsRepository {

    override suspend fun getHoldings(): List<Holding> {
        val bytes = Res.readBytes("files/investments.json")

        // 2. Convert bytes to a UTF-8 String
        val jsonString = bytes.decodeToString()

        // 3. Parse the string into your data class
        return Json.decodeFromString<List<Holding>>(jsonString)
    }
}