package com.maroondevelopment.networth.data.datasource

import com.maroondevelopment.networth.data.QuoteDto
import com.maroondevelopment.networth.network.HttpFactory
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

interface QuoteDataSource {

    suspend fun getQuote(symbol: String): QuoteDto
}

class RemoteQuoteDataSource : QuoteDataSource {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun getQuote(symbol: String): QuoteDto {
        val url = "https://stockinvest.us/api/v1/predictions/$symbol"
        val response: HttpResponse = HttpFactory.httpClient.get(url)

        // 2. Check for success status (200-299)
        if (response.status.isSuccess()) {
            return json.decodeFromString<QuoteDto>(response.bodyAsText())
        } else {
            return QuoteDto(
                ticker = symbol,
                price = 0.0
            )
        }
    }
}